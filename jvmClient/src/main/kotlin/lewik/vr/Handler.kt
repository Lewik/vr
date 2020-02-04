package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

@Service
class Handler @Autowired constructor(
    private val sendGateway: SendGateway,
    private val uiGateway: UiGateway
) {


    private var sessionUuid = ""
    private var doReceive = false

    private fun sendGetSessionUpdate(sessionLastUpdate: Long) {
        sendGateway.send(
            NetworkPacket(
                getSessionUpdate = GetSessionUpdate(
                    sessionUuid = sessionUuid,
                    sessionLastUpdate = sessionLastUpdate
                )
            )
        )
    }


    fun handleFromServer(payload: NetworkPacket) {
        when {
            payload.sessionUpdate != null -> {
                payload.sessionUpdate.partFrames.forEach {
                    uiGateway.send(it)
                }
                payload.sessionUpdate.mousePosition
                    ?.also { uiGateway.send(it) }

                if (doReceive) {
                    Timer("", false).schedule(100L) { sendGetSessionUpdate(payload.sessionUpdate.sessionLastUpdate) }

                }
            }
        }
    }

    fun toggleReceive(sessionUuid: String) {
        this.sessionUuid = sessionUuid
        if (doReceive) {
            doReceive = false
        } else {
            doReceive = true
            sendGetSessionUpdate(Instant.now().toEpochMilli())
        }
    }
}
