package spike.rabbitmq

import akka.actor.ActorRef
import com.rabbitmq.client.Channel
import com.thenewmotion.akka.rabbitmq._

object Consumer extends App with SystemActor {

  def setupSubscriber(channel: Channel, self: ActorRef) {
    channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
    channel.queueBind(queueName, exchange, routingKey)
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        println(s"consumer receiving ${fromBytes(body)}")
      }
    }
    channel.basicConsume(queueName, true, consumer)
  }

  connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some("subscriber"))

  def fromBytes(x: Array[Byte]) = new String(x, "UTF-8").toLong
}
