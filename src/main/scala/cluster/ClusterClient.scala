package cluster

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp}
import com.typesafe.config.ConfigFactory



/**
  * Created by pako on 20/5/17.
  */
class ClusterClient extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberEvent], classOf[MemberUp])

  override def receive = {
    case MemberUp(member) => println(s"New member of the system $member.address")
    case me : MemberEvent => println("Member event " + me)
    case Msg.SendMe(n) => (1 to n).foreach(z => sender ! Msg.Msg(z))
    case Msg.Msg(n) => print(s"$n ")
    case x => print(s"Unhandled $x ")
  }

  override def postStop() = cluster.unsubscribe(self)
}

object ClusterClient {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(args)
    system.actorOf(Props[ClusterClient], name = "ClusterClient")
  }
}

class BadGuy extends Actor {

  val cluster = Cluster(context.system)

  var client  = Seq.empty[ActorRef]

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberEvent], classOf[MemberUp])

  override def receive: Receive = {
    case MemberUp(member) =>
      context.actorSelection(RootActorPath(member.address) / "user" / "ClusterClient") ! Msg.SendMe(100)
    case x => println(x)

  }
}

object BadGuy {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(args)
    system.actorOf(Props[BadGuy], name = "BadGuy")
  }
}


object Msg {
  case class SendMe(n : Int)
  case class Msg(count : Int)

  def buildActorSystem(args : Array[String]) = {
    println("Start..... " + args.toSeq)
    var port = "6666"

    if (!args.isEmpty)
      port = args(0)

    println(s"Port $port")

    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.load())

    val system = ActorSystem.create("ActorSystem", config)
    system
  }
}

