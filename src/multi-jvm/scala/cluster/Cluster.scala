
package cluster

import akka.actor.{ActorSystem, Props}


object ClusterMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem()
    system.actorOf(Props[ClusterClient], name = "ClusterClient")
  }
}

object ClusterMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(6677)
    system.actorOf(Props[ClusterClient], name = "ClusterClient")
  }
}

object ClusterMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(6678)
    system.actorOf(Props[BadGuy], name = "BadGuy")
  }
}

object ClusterMultiJvmNode4 {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = Msg.buildActorSystem(6679)
    (1 to 100).foreach(x => system.actorOf(Props[BadGuy], name = "BadGuy" + x))
  }
}
