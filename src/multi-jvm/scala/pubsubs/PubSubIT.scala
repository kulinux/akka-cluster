package pubsubs

import akka.actor.{ActorSystem, Props}
import cluster.Msg
import pubsubs.MsgsToPublish.MsgToPublish

/**
  * Created by pako on 24/5/17.
  */
object PubSubMultiJvmNode1 extends App {
  val system: ActorSystem = Msg.buildActorSystem()
  system.actorOf(Props[PubSubClient], "actor1")
}

object PubSubMultiJvmNode2 extends App {
  val system: ActorSystem = Msg.buildActorSystem(7799)
  system.actorOf(Props[PubSubClient], "actor2")
}

object PubSubMultiJvmNode3 extends App {
  val system: ActorSystem = Msg.buildActorSystem(7788)
  val actorPub = system.actorOf(Props[PubSubPublisher], "actor3")
  Thread.sleep(10000)
  actorPub ! MsgToPublish("Este es un mensaje para toda la cola")
}
