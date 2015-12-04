package spike.rabbitmq

import com.thenewmotion.akka.rabbitmq.ConnectionFactory

trait AppConfiguration {

    val factory = new ConnectionFactory()
    factory.setHost("127.0.0.1")
    factory.setPort(5672)
    factory.setUsername("guest")
    factory.setPassword("guest")
    factory.setAutomaticRecoveryEnabled(true)
    factory.setTopologyRecoveryEnabled(true)

    val exchange = "my_exchange"
    val queueName = "my_queue"
    val fanout = "fanout"
    val emptyRoutingKey = ""
    val durable = true
    val noExclusive = false
    val noAutoDelete = false
}
