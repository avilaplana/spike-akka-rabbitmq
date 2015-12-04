package spike.rabbitmq

import akka.actor._
import com.rabbitmq.client.{DefaultConsumer, Channel, MessageProperties}
import com.thenewmotion.akka.rabbitmq._
import spike.rabbitmq.ProducerActor.Message


object Producer extends AppConfiguration with App {

  import MessageProperties._

  val system = ActorSystem()

  val connManager: ActorRef = system.actorOf(ConnectionActor.props(factory), "rabbitmq")

  connManager ! CreateChannel(
    ChannelActor.props({ (c, ref) => c.exchangeDeclare(exchange, fanout, durable) }),
    Some("publisher")
  )
  val publisher = system.actorSelection("/user/rabbitmq/publisher")

  val producer = system.actorOf(
    ProducerActor.props(
    publisher, { s => ChannelMessage(_.basicPublish(exchange, emptyRoutingKey, PERSISTENT_TEXT_PLAIN, toBytes(s)), false) }
    ))

  private def toBytes(m: String) = m.getBytes("UTF-8")

  Range(1, Int.MaxValue).foreach(e => {
    println(s"Sending the event: $e")
    producer ! Message(e.toString)
    Thread.sleep(1000)
  }
  )
}


