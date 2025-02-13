package demo;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class B extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {
        return Props.create(B.class, B::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {
                    log.info("Received message: {} from sender: {}", msg, getSender());
                    getSender().tell("hi!", getSelf());
                })
                .build();
    }
}