package sample.cqrs

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config.{Config, ConfigFactory}
import sample.cqrs.application.ShoppingCartRoutes
import sample.cqrs.domain.ShoppingCart
import sample.cqrs.infrastructure.{EventProcessorSettings, ShoppingCartServer}

object Main {

  def main(args: Array[String]): Unit = {
    args.headOption match {
      case Some(portString) if portString.matches("""\d+""") =>
        val port = portString.toInt
        val httpPort = ("80" + portString.takeRight(2)).toInt
        startNode(port, httpPort)

      case None =>
        throw new IllegalArgumentException("port number, or cassandra required argument")
    }
  }

  def startNode(port: Int, httpPort: Int): Unit = {
    ActorSystem[Nothing](Guardian(), "Shopping", config(port, httpPort))
  }

  def config(port: Int, httpPort: Int): Config =
    ConfigFactory.parseString(s"""
      akka.remote.artery.canonical.port = $port
      shopping.http.port = $httpPort
       """).withFallback(ConfigFactory.load())

}

object Guardian {
  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { context =>
      val system = context.system
      val settings = EventProcessorSettings(system)
      val httpPort = context.system.settings.config.getInt("shopping.http.port")

      ShoppingCart.init(system, settings)

      val routes = new ShoppingCartRoutes()(context.system)
      new ShoppingCartServer(routes.shopping, httpPort, context.system).start()

      Behaviors.empty
    }
  }
}
