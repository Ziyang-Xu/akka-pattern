package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TellToAndForget {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("system");
        final LoggingAdapter log = Logging.getLogger(system, "main");

        log.info("Creating actor B");
        final ActorRef b = system.actorOf(B.props(), "b");

        log.info("Creating actor Transmitter");
        final ActorRef transmitter = system.actorOf(Transmitter.props(b), "transmitter");

        log.info("Creating actor A");
        final ActorRef a = system.actorOf(A.props(transmitter, b), "a");

        log.info("Sending transmitter and B addresses to actor A");
        a.tell(new A.InitMessage(transmitter, b), ActorRef.noSender());

        try {
            waitBeforeTerminate();
        } catch (InterruptedException e) {
            log.error(e, "InterruptedException occurred");
        } finally {
            log.info("Terminating actor system");
            system.terminate();
        }
    }

    public static void waitBeforeTerminate() throws InterruptedException {
        Thread.sleep(5000);
    }
}