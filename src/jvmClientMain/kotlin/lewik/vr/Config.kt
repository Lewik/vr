package lewik.vr

import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.dump
import kotlinx.serialization.load
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory


@EnableIntegration
@IntegrationComponentScan
@Configuration
class Config {
    @Bean
    fun send(
        clientOutputConnectionFactory: TcpNioClientConnectionFactory
    ) = IntegrationFlows.from(SendGateway::class.java)
        .transform { packet: Packet -> CBOR.dump(packet) }
        .handle(Tcp.outboundAdapter(clientOutputConnectionFactory))
        .get()!!

    @Bean
    fun input(
        clientInputConnectionFactory: TcpNioClientConnectionFactory,
        uiController: UiController
    ) = IntegrationFlows.from(Tcp.inboundAdapter(clientInputConnectionFactory))
        .transform { payload: ByteArray -> CBOR.load<Packet>(payload) }
        .handle { payload: Packet, _ -> uiController.updateWith(payload) }
        .get()!!


    @Bean
    fun clientInputConnectionFactory(): TcpNioClientConnectionFactory {
        val factory = TcpNioClientConnectionFactory("localhost", 11000)
        factory.isSingleUse = false
        factory.soTimeout = 300000
        return factory
    }

    @Bean
    fun clientOutputConnectionFactory(): TcpNioClientConnectionFactory {
        val factory = TcpNioClientConnectionFactory("localhost", 10000)
        factory.isSingleUse = false
        factory.soTimeout = 300000
        return factory
    }
}


@MessagingGateway
interface SendGateway {
    fun send(packet: Packet)
}