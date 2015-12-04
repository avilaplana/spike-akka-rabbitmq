package spike.rabbitmq

import akka.actor.{ActorRef, ActorSystem}
import com.rabbitmq.client.Channel
import com.thenewmotion.akka.rabbitmq._

class Consumer(id: String, connManager: ActorRef) extends AppConfiguration {

  connManager ! CreateChannel(ChannelActor.props(setupSubscriber), Some(s"subscriber-$id"))

  private def setupSubscriber(channel: Channel, self: ActorRef) {
    channel.queueDeclare(queueName, durable, noExclusive, noAutoDelete, null)
    channel.queueBind(queueName, exchange, emptyRoutingKey)
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        println(s"consumer $id receiving ${fromBytes(body)}")
      }
    }
    channel.basicConsume(queueName, true, consumer)
  }

  private def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")
}


object ConsumerPool extends AppConfiguration with App {
  private val system = ActorSystem()
  private val connManager = system.actorOf(ConnectionActor.props(factory), "rabbitmq")

  Range(1, 5).map(id => new Consumer(s"CONSUMER-$id", connManager))
}
