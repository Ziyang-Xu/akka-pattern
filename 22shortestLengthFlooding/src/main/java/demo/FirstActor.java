package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirstActor extends UntypedAbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ArrayList<ActorRef> actorList;
    private Map<Integer, Integer> receivedMessages; // Store sequenceNumber and corresponding length

    public FirstActor() {
        this.actorList = new ArrayList<>();
        this.receivedMessages = new HashMap<>();
    }

    public static Props createActor() {
        return Props.create(FirstActor.class, FirstActor::new);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ActorRef) {
            this.actorList.add((ActorRef) message);
            log.info("[" + getSelf().path().name() + "] has the reference to [" + ((ActorRef) message).path().name() + "]");
        } else if (message instanceof MyMessage) {
            MyMessage myMessage = (MyMessage) message;
            int seqNumber = myMessage.sequenceNumber;
            int length = myMessage.length;

            if (!receivedMessages.containsKey(seqNumber) || length < receivedMessages.get(seqNumber)) {
                receivedMessages.put(seqNumber, length);
                myMessage.length += 1; // Increment length

                for (ActorRef actor : actorList) {
                    actor.tell(myMessage, getSelf());
                }
            }
        }
    }
}