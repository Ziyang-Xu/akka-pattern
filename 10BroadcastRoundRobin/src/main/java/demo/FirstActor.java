package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.RepointableActorRef;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.time.Duration;

public class FirstActor extends UntypedAbstractActor {
	private RepointableActorRef ref;
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public FirstActor() {
		getContext().system().scheduler().scheduleOnce(Duration.ofMillis(1000), getSelf(), "go",
				getContext().getSystem().getDispatcher(), ActorRef.noSender());
	}

	static public Props createActor() {
		return Props.create(FirstActor.class, FirstActor::new);
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof String) {
			if (message.equals("go")) {
				log.info("[" + getSelf().path().name() + "] received message '" + message + "' from ["
						+ getSender().path().name() + "]");
				// ask broadcaster to send m
				ref.tell("m", getSelf());
			}
		} else if (message instanceof RepointableActorRef) {
			ref = (RepointableActorRef) message;
		}
	}
}