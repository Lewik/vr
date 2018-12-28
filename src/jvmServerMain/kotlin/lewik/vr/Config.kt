package lewik.vr

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory
import org.springframework.integration.ip.tcp.serializer.TcpCodecs

@EnableIntegration
@Configuration
class Config {

    @Bean
    fun input(
        serverConnectionFactory: TcpNetServerConnectionFactory
    ) = IntegrationFlows
        .from(Tcp.inboundAdapter(serverConnectionFactory))
        .handle { payload: Any, _ -> println("Received");payload }
        .channel(MessageChannels.queue())
        .bridge { it.poller { p -> p.fixedRate(0) } }
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