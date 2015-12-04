package spike.rabbitmq

import akka.actor.ActorSystem
import com.thenewmotion.akka.rabbitmq.{ConnectionActor, ConnectionFactory}

/**
  * Created by payments on 04/12/15.
  */
trait SystemActor {

  val exchange = "my_exchange100"
  val queueName = "my_queue100"
  val routingKey = ""
  val durable = true
  val exclusive = false
  val autoDelete = false

  val system = ActorSystem()

  val factory = new ConnectionFactory()
  val config = com.typesafe.config.ConfigFactory.load().getConfig("rabbitmq")
  factory.setHost(config.getString("host"))
  factory.setPort(config.getInt("port"))
  factory.setUsername(config.getString("username"))
  factory.setPassword(config.getString("password"))
  factory.setAutomaticRecoveryEnabled(true)
  factory.setTopologyRecoveryEnabled(true)

  val connection = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
}

