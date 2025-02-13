package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Transmitter extends AbstractActor {
    private final ActorRef b;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static class Message {
        public final String content;
        public final ActorRef sender;

        public Message(String content, ActorRef sender) {
            this.content = content;
            this.sender = sender;
        }
    }

    public Transmitter(ActorRef b) {
        this.b = b;
    }

    public static Props props(ActorRef b) {
        return Props.create(Transmitter.class, () -> new Transmitter(b));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, msg -> {
                    log.info("Transmitting message: {} from sender: {}", msg.content, msg.sender);
                    b.tell(msg.content, msg.sender);
                })
                .build();
    }
}