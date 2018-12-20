package lewik.vr

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory

@EnableIntegration
@Configuration
class Config {

    @Bean
    fun input(
        serverInputConnectionFactory: TcpNioServerConnectionFactory
    ) = IntegrationFlows.from(Tcp.inboundAdapter(serverInputConnectionFactory))
        .channel(outputChannel())
        .get()!!


    @Bean
    fun outputChannel() = MessageChannels.publishSubscribe()!!


    @Bean
    fun output(
        serverOutputConnectionFactory: TcpNioServerConnectionFactory
    ) = IntegrationFlows.from(outputChannel())
        .handle(Tcp.outboundAdapter(serverOutputConnectionFactory))
        .get()!!

    @Bean
    fun serverInputConnectionFactory(): TcpNioServerConnectionFactory {
        val factory = TcpNioServerConnectionFactory(10000)
        factory.isSingleUse = false
        factory.soTimeout = 300000
        return factory
    }

    @Bean
    fun serverOutputConnectionFactory(): TcpNioServerConnectionFactory {
        val factory = TcpNioServerConnectionFactory(11000)
        factory.isSingleUse = false
        factory.soTimeout = 300000
        return factory
    }
}