package distribute

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ddata.Replicator._
import akka.cluster.ddata.{DistributedData, ORSet, ORSetKey}
import distribute.DistributedActor.Tick

import scala.concurrent.duration._

/**
  * Created by pako on 25/5/17.
  */

object DistributedActor {
  case object Tick
}
class DistributedActor extends Actor with ActorLogging {

  val replicator = DistributedData(context.system).replicator

  implicit val cluster = Cluster(context.system)
  import context.dispatcher

  val dataKey = ORSetKey[String]("key")

  replicator ! Subscribe(dataKey, self)


  val tickTask = context.system.scheduler.schedule(5.seconds, 5.seconds, self, Tick)


  override def receive: Receive = {
    case Tick => {
      val s = System.currentTimeMillis().toString
      replicator ! Update(dataKey, ORSet.empty[String], WriteLocal)(_ + s)
    }
    case _: UpdateResponse[_] => // ignore

    case c @ Changed(dataKey) =>
      val data = c.get(dataKey)
      log.info("Current elements: {}", data)
  }
}
