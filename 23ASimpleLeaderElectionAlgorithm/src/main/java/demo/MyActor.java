package demo;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyActor extends UntypedAbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    ActorRef next = null;
    boolean parti = false;
    boolean elected = false;

    public MyActor() {
    }

    public static Props createActor() {
        return Props.create(MyActor.class, () -> {
            return new MyActor();
        });
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ActorRef) {
            next = (ActorRef) message;
        }
        if (message instanceof MyMessage) {
            MyMessage temp = (MyMessage) message;
            log.info("[" + getSelf().path().name() + "] received message "+temp.data+" from [" + getSender().path().name()+"]");

            // when the actor receives a "Start" message, 
            // it initiates the leader election by sending an "Election" message to the next actor
            if (temp.data.equals("Start")) {
                next.tell(new MyMessage("Election", Integer.parseInt(getSelf().path().name())), getSelf());
                parti = true;
            } 
            
            if (temp.data.equals("Elected")) {
                if (elected == false) {
                    next.tell(temp, getSender());
                    parti = false;
                } else {
                    log.info("Election is done");
                }
            }

            if (temp.data.equals("Election"))
            {
                int my_id = Integer.parseInt(getSelf().path().name());
                
                // if an actor receives an "Election" message from a higher ID actor, it forwards the message to the next actor
                if (temp.id > my_id) {
                    next.tell(temp, getSender());
                    parti = true;
                }

                // if it receives the message from a lower ID actor,
                // it decides whether to discard the message or forward it based on the parti flag.
                if (temp.id < my_id) {
                    if (parti == true) {
                        log.info("[" + getSelf().path().name() + "] discards the message from ["+ getSender().path().name() + "]");
                    } else {
                        next.tell(new MyMessage("Election", Integer.parseInt(getSelf().path().name())), getSelf());
                        parti = true;
                    }
                }
                // if it receives the message from an equal ID actor, 
                // it declares itself as the leader and sends an "Elected" message to the next actor
                 if (temp.id == my_id) {
                    elected = true;
                    parti = false;
                    log.info("[" + getSelf().path().name() + "] is the leader");
                    next.tell(new MyMessage("Elected", Integer.parseInt(getSelf().path().name())), getSelf());
                }
            }

        }

    }

}
