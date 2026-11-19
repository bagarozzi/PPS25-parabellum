package it.unibo.parabellum
package server

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.pekko.actor.{ActorSystem as ClassicActorSystem}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.persistence.jdbc.testkit.scaladsl.SchemaUtils
import scala.concurrent.Await
import scala.concurrent.duration.*

/**
 * Entry point of the server.
 *
 * Every argument is the port of a node of the cluster, and all the nodes run in this JVM:
 *  - no arguments: two nodes, on the ports of the seed nodes (see `application.conf`);
 *  - `2551 2552 2553`: three nodes, and so on.
 * To run every node in its own JVM, start the program once for each port.
 *
 * Every node also opens a gateway for the clients, on the HTTP port that goes with its port:
 * 2551 -> 8080, 2552 -> 8081 and so on.
 */
object ServerMain:

  private val SystemName = "ParabellumCluster"
  private val DefaultPorts = Seq(2551, 2552)
  private val FirstHttpPort = 8080

  def main(args: Array[String]): Unit =
    val ports = if args.isEmpty then DefaultPorts else args.toSeq.map(_.toInt)
    val base = ConfigFactory.load()
    if base.getBoolean("parabellum.persistence.create-schema") then createSchema(base)
    ports.foreach(startNode)

  /**
   * Creates the tables of the journal, if they do not exist. It is done once, before the nodes start, with
   * an actor system that only lives for this: if all the nodes did it, they would try at the same time.
   */
  private def createSchema(config: Config): Unit =
    given system: ClassicActorSystem =
      ClassicActorSystem("schema-setup", ConfigFactory.parseString("pekko.actor.provider = local").withFallback(config))
    try Await.result(SchemaUtils.createIfNotExists(), 30.seconds)
    finally system.terminate()

  private def startNode(port: Int): ActorSystem[ClusterNode.Command] =
    val config = ConfigFactory
      .parseString(s"pekko.remote.artery.canonical.port = $port")
      .withFallback(ConfigFactory.load())
    // only one node runs the smoke test, otherwise they would all start the same matches
    val smokeTestAfter = Option.when(port == DefaultPorts.head)(15.seconds)
    val httpPort = FirstHttpPort + (port - DefaultPorts.head)
    ActorSystem(ClusterNode(smokeTestAfter, Some(httpPort)), SystemName, config)
