package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class BroadcastRoundRobin {

	public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("system");
        final ActorRef a = system.actorOf(demo.FirstActor.createActor(), "a");
        final ActorRef b = system.actorOf(demo.SecondActor.createActor(), "b");
        final ActorRef c = system.actorOf(demo.ThirdActor.createActor(), "c");
        final ActorRef broadcaster = system.actorOf(main.java.demo.Broadcaster.createActor(), "broadcaster");

        a.tell(broadcaster, ActorRef.noSender());

        broadcaster.tell("join", b );
        broadcaster.tell("join", c);
        
        try {
            waitBeforeTerminate();
        } catch (Exception e) {

        } finally {
            system.terminate();
        }
    }

    public static void waitBeforeTerminate() throws InterruptedException {
        Thread.sleep(5000);
    }
}