package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.Random;

public class LeaderElection {
    public static void main(String[] args) {
        
        final ActorSystem system = ActorSystem.create("system");
        
        int num = 7;

        ActorRef[] actors = new ActorRef[num];

        for (int i = 0; i < num; i++) {
            actors[i] = system.actorOf(MyActor.createActor(), Integer.toString(i+1));
        }

        // this loop establishes a ring topology by having each actor send a message to the next actor in the array
        for (int i = 0; i < num; i++) {
            actors[i].tell(actors[(i + 1) % num], ActorRef.noSender());
        }
        
        Random random = new Random();

        int randomactor = random.nextInt(5);
        // a random actor is selected to start the leader election process by sending a "Start" message
        actors[randomactor].tell(new MyMessage("Start", randomactor + 1), ActorRef.noSender());

        try {
            waitBeforeTerminate();
        } 
        catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            system.terminate();
        }
        }
    
        public static void waitBeforeTerminate() throws InterruptedException {
            Thread.sleep(5000);
        }
    }
