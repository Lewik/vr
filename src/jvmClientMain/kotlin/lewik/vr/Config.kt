package lewik.vr

import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.dump
import kotlinx.serialization.load
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory
import org.springframework.integration.ip.tcp.serializer.TcpCodecs


@EnableIntegration
@IntegrationComponentScan
@Configuration
class Config {

    @Bean
    fun outputChannel() = MessageChannels.direct("outputChannel")!!

    @Bean
    fun send(
        clientConnectionFactory: TcpNetClientConnectionFactory,
        uiController: UiController
    ) = IntegrationFlows
        .from("outputChannel")
        .transform { packet: Packet -> CBOR().dump(packet) }
        .channel(MessageChannels.queue())
        .bridge { it.poller { p -> p.fixedRate(0) } }
        .handle { payload: ByteArray, _ -> println("Sending ${payload.size} ${payload.size.toShort()}");payload }
        .handle(TcpSendingMessageHandler().also {
            it.setConnectionFactory(clientConnectionFactory)
            it.isClientMode = true
        })
        .get()!!


    @Bean
    fun input(
        clientConnectionFactory: TcpNetClientConnectionFactory,
        uiController: UiController
    ) = IntegrationFlows
        .from(Tcp.inboundAdapter(clientConnectionFactory).also {
            it.clientMode(true)
            // it.autoStartup(true)
        })
        .handle { payload: ByteArray, _ -> println("Receiving ${payload.size}");payload }
        .channel(MessageChannels.queue())
        .bridge { it.poller { p -> p.fixedRate(0) } }
        .transform { payload: ByteArray -> CBOR.load<Packet>(payload) }
        .handle { payload: Packet, _ -> uiController.updateWith(payload);null }
        .get()!!


    @Bean
    fun clientConnectionFactory(): TcpNetClientConnectionFactory {
        val factory = TcpNetClientConnectionFactory("localhost", 61000)
        factory.isSingleUse = false
        factory.deserializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Short.MAX_VALUE.toInt() }
        factory.serializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Short.MAX_VALUE.toInt() }
        return factory
    }
}


@MessagingGateway
interface SendGateway {
    @Gateway(requestChannel = "outputChannel")
    fun send(packet: Packet)
}