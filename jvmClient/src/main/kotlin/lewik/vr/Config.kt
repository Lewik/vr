package lewik.vr

import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.dump
import kotlinx.serialization.load
import org.springframework.beans.factory.annotation.Value
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
        uiController: UiController,
        speedCalculator: SpeedCalculator,
        handler: Handler
    ) = IntegrationFlows
        .from("outputChannel")
        .handle { payload: NetworkPacket, _ ->
            val string = when {
                payload.getSessionUpdate != null -> "getSessionUpdate"
                payload.sessionUpdate != null -> "sessionUpdate" + payload.sessionUpdate.partFrames.size
                else -> "unknown"
            }
            println("Sending $string")
            payload
        }
        .transform { networkPacket: NetworkPacket -> CBOR().dump(networkPacket).compress() }
        .handle(Tcp.outboundGateway(clientConnectionFactory))
        //.channel(MessageChannels.queue())
        //.bridge { it.poller { p -> p.fixedRate(0) } }
        .transform { payload: ByteArray -> CBOR.load<NetworkPacket>(payload.decompress()) }
        .handle { payload: NetworkPacket, _ ->
            val string = when {
                payload.sessionUpdate != null -> "sessionUpdate"+ payload.sessionUpdate.partFrames.size
                payload.received != null -> "received"
                else -> "unknown"
            }
            println("Receiving $string")
            payload
        }
        .handle { payload: NetworkPacket, _ -> handler.handleFromServer(payload); null }
        .get()!!


    @Value("\${server_address}")
    private val serverAddress: String? = null

    @Value("\${server_port}")
    private val serverPort: Int? = null

    @Bean
    fun clientConnectionFactory(): TcpNetClientConnectionFactory {
        val factory = TcpNetClientConnectionFactory(serverAddress!!, serverPort!!)
        factory.isSingleUse = false
        factory.deserializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Int.MAX_VALUE.toInt() }
        factory.serializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Int.MAX_VALUE.toInt() }
        return factory
    }


    ////////////
    //UI
    ////////////
    @Bean
    fun uiChannel() = MessageChannels.queue("uiChannel")!!

    @Bean
    fun uiInput(
        uiController: UiController
    ) = IntegrationFlows
        .from(uiChannel())
        .bridge { it.poller { p -> p.fixedRate(0) } }
        .handle { payload: UiPacket, _ -> uiController.updateWith(payload);null }
        .get()!!

}


@MessagingGateway(name = "sendGateway")
interface SendGateway {
    @Gateway(requestChannel = "outputChannel")
    fun send(networkPacket: NetworkPacket)
}


@MessagingGateway(name = "uiGateway")
interface UiGateway {
    @Gateway(requestChannel = "uiChannel")
    fun send(packet: UiPacket)
}
