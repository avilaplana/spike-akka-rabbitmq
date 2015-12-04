package spike.rabbitmq

import akka.actor._
import com.rabbitmq.client.{DefaultConsumer, Channel}
import com.thenewmotion.akka.rabbitmq._

object ProducerActor {

  case class Message(m: String)

  def props(publisher: ActorSelection, channelMessage: String => ChannelMessage) =
    Props(new ProducerActor(publisher, channelMessage))
}

class ProducerActor(publisher: ActorSelection, channelMessage: String => ChannelMessage) extends Actor with ActorLogging {

  import ProducerActor._

  override def receive: Receive = {
    case Message(m) => publisher ! channelMessage(m)
  }
}
