package lewik.vr

import kotlinx.serialization.Serializable


@Serializable
data class NetworkPacket(
    val partFrame: PartFrame? = null,
    val mousePosition: MousePosition? = null
)


const val DEFAULT_PART_FRAME_WIDTH = 50
const val DEFAULT_PART_FRAME_HEIGHT = 50

sealed class UiPacket

@Serializable
data class PartFrame(
    val width: Int?, //null - default
    val height: Int?, //null - default
    val startX: Int,
    val startY: Int,
    val colors: List<Int?>
) : UiPacket()


@Serializable
data class MousePosition(
    val point: Pair<Int, Int>
) : UiPacket()


@Serializable
data class Speed(
    val speed: Int
) : UiPacket()