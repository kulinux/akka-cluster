package seed

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import cluster.Msg
import com.typesafe.config.ConfigFactory

class SimpleEcho extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent], classOf[MemberUp])
    mediator ! Subscribe("content", self)
  }

  override def receive = {
    case s: String => {
      log.info(s"Got $s")
      Thread.sleep(5000)
      mediator ! Publish("content", s"Pong $self")
    }
    case SubscribeAck(Subscribe("content", None, self)) => {
      log.info("subscribing")
      Thread.sleep(5000)
      mediator ! Publish("content", s"Ping $self")
    }

  }

}


object Seed extends App {
  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 6666).
    withFallback(ConfigFactory.load())
  val system = ActorSystem.create("ActorSystem", config)
}

object ClusterMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(6677)
    system.actorOf(Props[SimpleEcho], name = "ClusterClient1")
  }
}


object ClusterMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(6678)
    system.actorOf(Props[SimpleEcho], name = "ClusterClient2")
  }
}

