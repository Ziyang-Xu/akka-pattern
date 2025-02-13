package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ControlledFlooding {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
        int [][] matrix = {{0,1,1,0,0},
                           {0,0,0,1,0},
                           {0,0,0,1,0},
                           {0,0,0,0,1},
                           {0,1,0,0,0}};

        ActorRef [] actors = new ActorRef[matrix[0].length];

        for(int i = 0; i<matrix[0].length; i++){
            actors[i] = system.actorOf(demo.FirstActor.createActor(), Integer.toString(i+1));
        }

        for(int i = 0; i<matrix[0].length; i++){
            for(int j = 0; j<matrix[0].length; j++){
                if(matrix[i][j]==1)
                    actors[i].tell(actors[j], ActorRef.noSender());
            }
        }
        
        actors[0].tell(new demo.MyMessage("m, seqNb = 0", 0), ActorRef.noSender());
        actors[0].tell(new demo.MyMessage("m, seqNb = 1", 1), ActorRef.noSender());
        actors[0].tell(new demo.MyMessage("m, seqNb = 2", 2), ActorRef.noSender());
        // each actor will receive the message only once, thanks to seq number no infinite loop
	    try {
			waitBeforeTerminate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			system.terminate();
		}
	}

	public static void waitBeforeTerminate() throws InterruptedException {
		Thread.sleep(5000);
	}

}
