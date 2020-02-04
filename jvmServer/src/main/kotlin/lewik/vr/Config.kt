package lewik.vr

import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.dump
import kotlinx.serialization.load
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.dsl.Tcp
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory
import org.springframework.integration.ip.tcp.serializer.TcpCodecs
import org.springframework.messaging.MessageHeaders
import org.springframework.stereotype.Service

@EnableIntegration
@Configuration
class Config {


    @Bean
    fun input(
        inputConnectionFactory: TcpNetServerConnectionFactory,
        handler: Handler
    ) = IntegrationFlows
        .from(Tcp.inboundGateway(inputConnectionFactory))
        .handle { payload: ByteArray, _ -> CBOR.load<NetworkPacket>(payload.decompress()) }
        .handle { payload: NetworkPacket, _ ->
            val string = when {
                payload.getSessionUpdate != null -> "getSessionUpdate"
                payload.sessionUpdate != null -> "sessionUpdate"
                payload.received != null -> "received"
                else -> "unknown"
            }
            println("Receiving $string")
            payload
        }
        .handle { payload: NetworkPacket, headers -> handler.input(payload, headers) }
        .handle { payload: NetworkPacket, _ ->
            val string = when {
                payload.sessionUpdate != null -> "sessionUpdate"
                payload.received != null -> "received"
                else -> "unknown"
            }
            println("Sending $string")
            payload
        }
        .handle { payload: NetworkPacket, _ -> CBOR().dump(payload).compress() }
        .get()!!

    @Bean
    fun serverConnectionFactory(): TcpNetServerConnectionFactory {
        val factory = TcpNetServerConnectionFactory(61000)
        factory.deserializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Int.MAX_VALUE }
        factory.serializer = TcpCodecs.lengthHeader4().also { it.maxMessageSize = Int.MAX_VALUE }
        factory.isSingleUse = false
        return factory
    }
}

@Service
open class Handler @Autowired constructor(
    private val storage: Storage
) {
    fun input(data: NetworkPacket, headers: MessageHeaders): NetworkPacket = when {
        data.sessionUpdate != null -> {
            data.sessionUpdate.mousePosition
                ?.also { storage.addMousePosition(it) }

            data.sessionUpdate.partFrames
                .forEach { storage.addPartFrame(it) }

            NetworkPacket(
                received = Received(
                    sessionUuid = data.sessionUpdate.mousePosition!!.sessionUuid,
                    sessionLastUpdate = data.sessionUpdate.mousePosition!!.createTime
                )
            )
        }
        data.getSessionUpdate != null -> {
            val sessionUpdate = storage.getSessionUpdates(
                data.getSessionUpdate.sessionUuid,
                data.getSessionUpdate.sessionLastUpdate
            )

            NetworkPacket(sessionUpdate = sessionUpdate)
        }
        else -> {
            NetworkPacket(
                received = Received(
                    sessionUuid = "-",
                    sessionLastUpdate = 0L
                )
            )
        }
    }
}
