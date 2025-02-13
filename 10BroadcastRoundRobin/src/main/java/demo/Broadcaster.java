package main.java.demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Vector;

public class Broadcaster extends UntypedAbstractActor {
    
    Vector<ActorRef> refs = new Vector<>();
    
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props createActor() {
        return Props.create(Broadcaster.class, Broadcaster::new);
    }

    @Override
    public void onReceive(Object message) throws Throwable, Throwable {
        if (message instanceof String & message.equals("m")) {
			log.info("[" + getSelf().path().name() + "] received message '" + message +"' from ["+ getSender().path().name() + "]");
			log.info("Broadcasting....");
            refs.forEach(x -> {
                x.tell(message, getSelf());
            });
        } else if (message instanceof String & message.equals("join")) {
            if (getSender() != null)
                refs.add(getSender());
				log.info("[" + getSelf().path().name() + "] received message '" + message +"' from ["+ getSender().path().name() + "]");
        }
    }
}