package lewik.vr

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory
import org.springframework.integration.ip.tcp.serializer.TcpCodecs

@EnableIntegration
@Configuration
class Config {

    @Bean
    fun input(
        serverConnectionFactory: TcpNioServerConnectionFactory
    ) = IntegrationFlows
        .from(Tcp.inboundAdapter(serverConnectionFactory))
        .handle { payload: Any, _ -> println("Received");payload }
        .channel(MessageChannels.queue())
        .bridge { it.poller { p -> p.fixedRate(0) } }
        .handle(Tcp.outboundAdapter(serverConnectionFactory))
        .get()!!

    @Bean
    fun serverConnectionFactory(): TcpNioServerConnectionFactory {
        val factory = TcpNioServerConnectionFactory(61000)
        factory.deserializer = TcpCodecs.lengthHeader4()
        factory.serializer = TcpCodecs.lengthHeader4()
        factory.isSingleUse = false
        return factory
    }
}