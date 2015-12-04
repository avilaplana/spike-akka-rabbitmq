package spike.rabbitmq

import akka.actor.ActorRef
import com.rabbitmq.client.Channel
import com.thenewmotion.akka.rabbitmq._

class Consumer(id: String) extends SystemActor {

  println(s"Consumer $id starting.....")

  connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some(s"subscriber-$id"))

  private def setupSubscriber(channel: Channel, self: ActorRef) {
    channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
    channel.queueBind(queueName, exchange, routingKey)
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        println(s"consumer $id receiving ${fromBytes(body)}")
      }
    }
    channel.basicConsume(queueName, true, consumer)
  }
  private def fromBytes(x: Array[Byte]) = new String(x, "UTF-8").toLong
}



object ConsumerPool extends App {
  Range(1,5).map(id => new Consumer(s"CONSUMER-$id"))
}
