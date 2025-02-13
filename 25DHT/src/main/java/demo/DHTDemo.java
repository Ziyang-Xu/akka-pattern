package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class DHTDemo {

    // ===== Message Types =====

    // Lookup message: look up the data corresponding to a key
    public static class Lookup {
        public final String key;
        public Lookup(String key) {
            this.key = key;
        }
    }

    // LookupResponse message: return the lookup result
    public static class LookupResponse {
        public final String key;
        public final String value;
        public LookupResponse(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    // Insert message: insert a key-value pair
    public static class Insert {
        public final String key;
        public final String value;
        public Insert(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    // Join message: a new node requests to join the network
    public static class Join {
        public final String newNodeId;      // Binary id of the new node
        public final ActorRef newNode;      // ActorRef of the new node
        public Join(String newNodeId, ActorRef newNode) {
            this.newNodeId = newNodeId;
            this.newNode = newNode;
        }
    }

    // ChildAssignment message: parent node informs the new node of its parent node information
    public static class ChildAssignment {
        public final String parentId;
        public final boolean isLeftChild; // true indicates left child, false indicates right child
        public ChildAssignment(String parentId, boolean isLeftChild) {
            this.parentId = parentId;
            this.isLeftChild = isLeftChild;
        }
    }

    // ===== DHT Node Actor =====

    public static class DHTNode extends AbstractActor {
        private final String id;  // Binary id of the node, e.g., "1010"
        private ActorRef parent;
        private ActorRef leftChild;
        private ActorRef rightChild;
        // If it is a leaf node, store data; non-leaf nodes are mainly used for forwarding
        private Map<String, String> data;

        public DHTNode(String id) {
            this.id = id;
            this.data = new HashMap<>();
        }

        public static Props props(String id) {
            return Props.create(DHTNode.class, () -> new DHTNode(id));
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Lookup.class, this::handleLookup)
                    .match(Insert.class, this::handleInsert)
                    .match(Join.class, this::handleJoin)
                    .match(ChildAssignment.class, this::handleChildAssignment)
                    .build();
        }

        /**
         * Handle lookup request
         */
        private void handleLookup(Lookup lookup) {
            System.out.println("Node " + (id.isEmpty() ? "root" : id) + " received Lookup(key=" + lookup.key + ")");
            if (isLeaf()) {
                String value = data.get(lookup.key);
                System.out.println("Node " + (id.isEmpty() ? "root" : id) + " is a leaf node, returning value=" + value);
                getSender().tell(new LookupResponse(lookup.key, value), getSelf());
            } else {
                int currentDepth = id.length();
                if (lookup.key.length() > currentDepth) {
                    char nextBit = lookup.key.charAt(currentDepth);
                    if (nextBit == '0' && leftChild != null) {
                        System.out.println("Node " + (id.isEmpty() ? "root" : id) + " forwards Lookup to left child");
                        leftChild.forward(lookup, getContext());
                        return;
                    } else if (nextBit == '1' && rightChild != null) {
                        System.out.println("Node " + (id.isEmpty() ? "root" : id) + " forwards Lookup to right child");
                        rightChild.forward(lookup, getContext());
                        return;
                    }
                }
                if (parent != null) {
                    System.out.println("Node " + (id.isEmpty() ? "root" : id) + " forwards Lookup to parent");
                    parent.forward(lookup, getContext());
                } else {
                    System.out.println("Node " + (id.isEmpty() ? "root" : id) + " cannot forward Lookup further");
                    getSender().tell(new LookupResponse(lookup.key, null), getSelf());
                }
            }
        }

        /**
         * Handle insert request
         */
        private void handleInsert(Insert insert) {
            System.out.println("Node " + (id.isEmpty() ? "root" : id) + " received Insert(key=" + insert.key + ", value=" + insert.value + ")");
            if (isLeaf()) {
                data.put(insert.key, insert.value);
                System.out.println("Node " + (id.isEmpty() ? "root" : id) + " stored key=" + insert.key);
            } else {
                int currentDepth = id.length();
                if (insert.key.length() > currentDepth) {
                    char nextBit = insert.key.charAt(currentDepth);
                    if (nextBit == '0' && leftChild != null) {
                        System.out.println("Node " + (id.isEmpty() ? "root" : id) + " forwards Insert to left child");
                        leftChild.forward(insert, getContext());
                        return;
                    } else if (nextBit == '1' && rightChild != null) {
                        System.out.println("Node " + (id.isEmpty() ? "root" : id) + " forwards Insert to right child");
                        rightChild.forward(insert, getContext());
                        return;
                    }
                }
                // Store directly if no suitable child node is found (as a fallback strategy)
                data.put(insert.key, insert.value);
                System.out.println("Node " + (id.isEmpty() ? "root" : id) + " stored key=" + insert.key + " (fallback strategy)");
            }
        }

        /**
         * Handle new node join request
         */
        private void handleJoin(Join join) {
            System.out.println("Node " + (id.isEmpty() ? "root" : id) + " received Join(newNodeId=" + join.newNodeId + ")");
            // If the new node id matches the prefix of this node's id, the new node should be in this node's subtree
            if (join.newNodeId.startsWith(id)) {
                int pos = id.length();
                if (join.newNodeId.length() > pos) {
                    char bit = join.newNodeId.charAt(pos);
                    if (bit == '0') {
                        if (leftChild == null) {
                            leftChild = join.newNode;
                            join.newNode.tell(new ChildAssignment(id, true), getSelf());
                            System.out.println("Node " + (id.isEmpty() ? "root" : id) + " assigned new node " + join.newNodeId + " as left child");
                        } else {
                            leftChild.forward(join, getContext());
                        }
                    } else if (bit == '1') {
                        if (rightChild == null) {
                            rightChild = join.newNode;
                            join.newNode.tell(new ChildAssignment(id, false), getSelf());
                            System.out.println("Node " + (id.isEmpty() ? "root" : id) + " assigned new node " + join.newNodeId + " as right child");
                        } else {
                            rightChild.forward(join, getContext());
                        }
                    }
                }
            } else {
                if (parent != null) {
                    parent.forward(join, getContext());
                } else {
                    System.out.println("Join(newNodeId=" + join.newNodeId + ") cannot be routed to a suitable node");
                }
            }
        }

        /**
         * Handle parent node assignment message
         */
        private void handleChildAssignment(ChildAssignment assignment) {
            this.parent = getSender();
            System.out.println("Node " + (id.isEmpty() ? "root" : id) + " assigned parent node " + assignment.parentId);
        }

        /**
         * Determine if it is a leaf node (no child nodes)
         */
        private boolean isLeaf() {
            return leftChild == null && rightChild == null;
        }
    }

    // ===== Test Client Actor =====
    // This actor is used to send Lookup requests and print the returned results
    public static class TestClient extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(LookupResponse.class, response -> {
                        System.out.println("TestClient received LookupResponse: key=" + response.key + ", value=" + response.value);
                    })
                    .build();
        }
        public static Props props() {
            return Props.create(TestClient.class, TestClient::new);
        }
    }

    // ===== main method =====

    public static void main(String[] args) throws Exception {
        // Create ActorSystem
        ActorSystem system = ActorSystem.create("DHTSystem");

        // Create root node, using an empty string to represent the root (entire key space)
        ActorRef root = system.actorOf(DHTNode.props(""), "root");

        // Create other nodes, e.g., id "0" and "1"
        ActorRef node0 = system.actorOf(DHTNode.props("0"), "node0");
        ActorRef node1 = system.actorOf(DHTNode.props("1"), "node1");

        // Let nodes join the network: send Join messages to the root node
        root.tell(new Join("0", node0), ActorRef.noSender());
        root.tell(new Join("1", node1), ActorRef.noSender());

        // Wait 1 second to ensure Join messages are processed
        Thread.sleep(1000);

        // Create a TestClient to receive lookup responses
        ActorRef client = system.actorOf(TestClient.props(), "client");

        // Insert test data: e.g., key "1010" corresponds to value "Hello DHT!"
        String testKey = "1010";
        root.tell(new Insert(testKey, "Hello DHT!"), ActorRef.noSender());

        // Wait 1 second to ensure data insertion is complete
        Thread.sleep(1000);

        // Initiate a lookup request, sent by the client (so LookupResponse will return to the client)
        // Note: specify the sender as the client through the tell method
        root.tell(new Lookup(testKey), client);

        // Wait enough time to observe the output, then shut down the system
        Thread.sleep(3000);
        system.terminate();
    }
}