package pubsubs

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import pubsubs.MsgsToPublish.MsgToPublish

/**
  * Created by pako on 22/5/17.
  */
trait ClusterRef extends Actor {
  val cluster = Cluster(context.system)

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("content", self)

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberEvent], classOf[MemberUp])

  override def postStop(): Unit =
    cluster unsubscribe self
}

object PubSubClient {
  case class PublishMsg(msg : String)
}

class PubSubClient extends Actor with ClusterRef {

  override def receive: Receive = {
   case x : String => println(s"messsage $x")
   case SubscribeAck(ack) => println(s"subscribe ok $ack")
  }
}

object MsgsToPublish {
  case class MsgToPublish(s : String)
}
class PubSubPublisher extends Actor with ClusterRef {

  override def receive: Receive = {
    case MsgToPublish(s) => mediator ! Publish("content", s)
  }

}
