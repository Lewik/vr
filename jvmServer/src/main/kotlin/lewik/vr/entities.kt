package lewik.vr

import kotlinx.serialization.Serializable


@Serializable
data class NetworkPacket(
    val sessionUpdate: SessionUpdate? = null,
    val getSessionUpdate: GetSessionUpdate? = null,
    val received: Received? = null
)

@Serializable
data class Received(
    val sessionUuid: String,
    val sessionLastUpdate: Long
)

@Serializable
data class GetSessionUpdate(
    val sessionUuid: String,
    val sessionLastUpdate: Long
)

@Serializable
data class SessionUpdate(
    val partFrames: List<PartFrame>,
    val mousePosition: MousePosition? = null,
    val sessionLastUpdate: Long
)


const val DEFAULT_PART_FRAME_WIDTH = 50
const val DEFAULT_PART_FRAME_HEIGHT = 50

sealed class UiPacket {
    abstract val sessionUuid: String
    abstract val createTime: Long
}

@Serializable
data class PartFrame(
    override val sessionUuid: String,
    override val createTime: Long,
    val width: Int?, //null - default
    val height: Int?, //null - default
    val startX: Int,
    val startY: Int,
    val colors: List<Int?>
) : UiPacket()


@Serializable
data class MousePosition(
    override val sessionUuid: String,
    override val createTime: Long,
    val point: Pair<Int, Int>
) : UiPacket()


@Serializable
data class Speed(
    override val sessionUuid: String,
    override val createTime: Long,
    val speed: Int
) : UiPacket()
