package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class A extends AbstractActor {
    private ActorRef transmitter;
    private ActorRef b;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static class InitMessage {
        public final ActorRef transmitter;
        public final ActorRef b;

        public InitMessage(ActorRef transmitter, ActorRef b) {
            this.transmitter = transmitter;
            this.b = b;
        }
    }

    public static Props props(ActorRef transmitter, ActorRef b) {
        return Props.create(A.class, () -> new A(transmitter, b));
    }

    public A(ActorRef transmitter, ActorRef b) {
        this.transmitter = transmitter;
        this.b = b;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InitMessage.class, msg -> {
                    this.transmitter = msg.transmitter;
                    this.b = msg.b;
                    log.info("Starting communication");
                    transmitter.tell(new Transmitter.Message("hello", getSelf()), getSelf());
                })
                .matchEquals("hi!", msg -> {
                    log.info("Received response: {} from sender: {}", msg, getSender());
                })
                .build();
    }
}