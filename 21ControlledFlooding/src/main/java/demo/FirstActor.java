package demo;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import java.util.*;

public class FirstActor extends UntypedAbstractActor{

	
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ArrayList<ActorRef> actorList;
    ArrayList<Integer> sequenceNumberList;

	public FirstActor() {
        this.actorList = new ArrayList<ActorRef>();
        this.sequenceNumberList = new ArrayList<Integer>();;
    }

	public static Props createActor() {
		return Props.create(FirstActor.class, () -> {
			return new FirstActor();
		});
	} 

    @Override
	public void onReceive(Object message) throws Throwable {
        if(message instanceof ActorRef){
                actorList.add(((ActorRef)message));
                log.info("["+getSelf().path().name()+"] has the reference to ["+ ((ActorRef)message).path().name() +"]");
            }
        if(message instanceof MyMessage){
            MyMessage msg = (MyMessage) message;
            if(!sequenceNumberList.contains(msg.sequenceNumber)){
                // no duplicates
                sequenceNumberList.add(msg.sequenceNumber);
                Iterator<ActorRef> i = actorList.iterator();
                // send to each actor i have its reference
                while(i.hasNext()){
                    ActorRef element = i.next();
                    element.tell(msg, getSelf());
                }
                log.info("["+getSelf().path().name()+"] received a message "+ msg.message+ " from ["+ getSender().path().name() +"]");
            }
        }
    }
}