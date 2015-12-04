package spike.rabbitmq

import akka.actor.ActorRef
import com.rabbitmq.client.{Channel, MessageProperties}
import com.thenewmotion.akka.rabbitmq.{CreateChannel, ChannelActor, ChannelMessage}

object Producer extends App with SystemActor {

  def setupPublisher(channel: Channel, self: ActorRef) {
    channel.exchangeDeclare(exchange, "fanout", durable)
  }

  connection ! CreateChannel(ChannelActor.props(setupPublisher), Some("publisher"))
  val publisher = system.actorSelection("/user/rabbitmq/publisher")
  val msgs = Range(1, Int.MaxValue)
  msgs.foreach(e => {
    println(s"Sending the event: $e")
    publisher ! ChannelMessage(_.basicPublish(exchange, "", MessageProperties.PERSISTENT_TEXT_PLAIN, toBytes(e)), dropIfNoChannel = false)
    Thread.sleep(1000)
  }
  )

  def toBytes(x: Long) = x.toString.getBytes("UTF-8")

}
