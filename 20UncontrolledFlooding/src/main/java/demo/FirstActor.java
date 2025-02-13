package demo;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import java.util.*;

public class FirstActor extends UntypedAbstractActor{

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ArrayList<ActorRef> actor_list;

	public FirstActor() {
        this.actor_list = new ArrayList<ActorRef>();
    }

	public static Props createActor() {
		return Props.create(FirstActor.class, () -> {
			return new FirstActor();
        });
	} 

    @Override
	public void onReceive(Object message) throws Throwable {
        if(message instanceof ActorRef)
        {
            this.actor_list.add((ActorRef)message);
			log.info("["+getSelf().path().name()+"] has the reference to ["+ ((ActorRef)message).path().name() +"]");
        }
        if(message instanceof String)
        {
            String msg = (String) message;
            if(msg.equals("m")){
                Iterator<ActorRef> i = actor_list.iterator();
                while(i.hasNext()){
                    ActorRef element = i.next();
                    element.tell("m", getSelf());
                }
                log.info("["+getSelf().path().name()+"] received: "+ msg + " from ["+ getSender().path().name()+"]");
            }
        }
    }
} 