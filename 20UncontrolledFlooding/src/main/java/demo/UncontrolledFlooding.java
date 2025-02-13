package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class UncontrolledFlooding {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
        int [][] matrix1 = {{0,1,1,0,0},
                            {0,0,0,1,0},
                            {0,0,0,1,0},
                            {0,0,0,0,1},
                            {0,0,0,0,0}};
        int [][] matrix2 = {{0,1,1,0,0},
                            {0,0,0,1,0},
                            {0,0,0,1,0},
                            {0,0,0,0,1},
                            {0,1,0,0,0}};

        ActorRef [] actors = new ActorRef[matrix1[0].length];
        //A:1 B:2 C:3 D:4 E:5
        for(int i = 0; i<matrix1[0].length; i++){
            actors[i] = system.actorOf(FirstActor.createActor(), Integer.toString(i+1));
        }

        for(int i = 0; i<matrix1[0].length; i++){
            for(int j = 0; j<matrix1[0].length; j++){
                if(matrix1[i][j]==1)
                    //actors[i].tell("create", actors[j]);
                    actors[i].tell(actors[j], ActorRef.noSender());
            }
        }
        //A starts */
        actors[0].tell("m", ActorRef.noSender());

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
