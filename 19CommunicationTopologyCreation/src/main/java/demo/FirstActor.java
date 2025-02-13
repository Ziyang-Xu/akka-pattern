package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;

public class FirstActor extends UntypedAbstractActor{

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ArrayList<ActorRef> actor_list;
	public FirstActor() {
		this.actor_list = new ArrayList<ActorRef>();}

	public static Props createActor() {
		return Props.create(FirstActor.class, () -> {
			return new FirstActor();
		}); 
    }

	@Override
	public void onReceive(Object message) throws Throwable {
        if(message instanceof ActorRef){
			this.actor_list.add((ActorRef)message);
			log.info("["+getSelf().path().name()+"] has the reference to ["+ ((ActorRef)message).path().name() +"]");
        }
	}
}
