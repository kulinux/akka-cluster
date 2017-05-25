package distribute

import akka.actor.{ActorSystem, Props}
import cluster.Msg
import pubsubs.MsgsToPublish.MsgToPublish

/**
  * Created by pako on 24/5/17.
  */
object DistributedMultiJvmNode1 extends App {
  val system: ActorSystem = Msg.buildActorSystem()
  system.actorOf(Props[DistributedActor], "actor1")
}

object DistributedMultiJvmNode2 extends App {
  val system: ActorSystem = Msg.buildActorSystem(7799)
  system.actorOf(Props[DistributedActor], "actor2")
}

object DistributedMultiJvmNode3 extends App {
  val system: ActorSystem = Msg.buildActorSystem(7788)
  val actorPub = system.actorOf(Props[DistributedActor], "actor3")
}
