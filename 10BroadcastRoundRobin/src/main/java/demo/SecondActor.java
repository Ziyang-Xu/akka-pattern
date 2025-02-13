package demo;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SecondActor extends UntypedAbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props createActor() {
        return Props.create(SecondActor.class, SecondActor::new);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof String){
            log.info("[" + getSelf().path().name() + "] received message '" + message +"' from ["+ getSender().path().name() + "]");
        }
    }
}