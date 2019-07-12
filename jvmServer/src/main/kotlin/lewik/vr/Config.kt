package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.ip.IpHeaders
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory
import org.springframework.integration.ip.tcp.serializer.TcpCodecs
import org.springframework.integration.support.MutableMessageHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@EnableIntegration
@Configuration
class Config {


    @Bean
    fun queue() = MessageChannels.publishSubscribe("outputChannel").get()!!

    @Bean
    fun input(
        serverConnectionFactory: TcpNetServerConnectionFactory,
        splitter: Splitter
    ) = IntegrationFlows
        .from(Tcp.inboundAdapter(serverConnectionFactory))
        .handle { payload: Any, _ -> println("Received");payload }
        .handle { payload: ByteArray, headers -> splitter.split(payload, headers);null }
        .get()!!

    @MessagingGateway(name = "outputGateway")
    interface OutputGateway {
        @Gateway(requestChannel = "outputChannel")
        fun send(packet: Message<ByteArray>)
    }


    @Bean
    fun output(
        serverConnectionFactory: TcpNetServerConnectionFactory,
        splitter: Splitter
    ) = IntegrationFlows
        .from("outputChannel")
        .handle(Tcp.outboundAdapter(serverConnectionFactory))
        .get()!!

    @Bean
    fun serverConnectionFactory(): TcpNetServerConnectionFactory {
        val factory = TcpNetServerConnectionFactory(61000)
        factory.deserializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Short.MAX_VALUE.toInt() }
        factory.serializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Short.MAX_VALUE.toInt() }
        factory.isSingleUse = false
        return factory
    }
}

@Service
open class Splitter @Autowired constructor(
    private val serverConnectionFactory: TcpNetServerConnectionFactory,
    private val outputGateway: Config.OutputGateway
) {
    fun split(payload: ByteArray, headers: MessageHeaders) {
        val originalHeader = headers[IpHeaders.CONNECTION_ID] as String
        serverConnectionFactory.openConnectionIds
            .filter { it != originalHeader }
            .forEach { connectionId ->
                val message = MessageBuilder
                    .withPayload(payload)
                    .copyHeaders(headers)
                    .setHeader(IpHeaders.CONNECTION_ID, connectionId)
                    .build()
                outputGateway.send(message)

            }
    }
}