package pubsubs

import akka.actor.{ActorSystem, Props}
import cluster.Msg
import pubsubs.MsgsToPublish.MsgToPublish

/**
  * Created by pako on 24/5/17.
  */
object PubSubTopicMultiJvmNode1 extends App {
  val system: ActorSystem = Msg.buildActorSystem()
  system.actorOf(Props[PubSubClientWithTopic], "actor1")
}

object PubSubTopicMultiJvmNode2 extends App {
  val system: ActorSystem = Msg.buildActorSystem(7799)
  system.actorOf(Props[PubSubClientWithTopic], "actor2")
}

object PubSubTopicMultiJvmNode3 extends App {
  val system: ActorSystem = Msg.buildActorSystem(7788)
  val actorPub = system.actorOf(Props[PubSubPublisherTopic], "actor3")
  while(true) {
    Thread.sleep(10000)
    actorPub ! MsgToPublish("Este es un mensaje para solo uno")
  }
}
