package lewik.vr

import kotlinx.serialization.Serializable


interface UiPacket

@Serializable
data class NetworkPacket(
    val partFrame: PartFrame? = null,
    val deltaFrame: DeltaFrame? = null
)

interface VrFrame : UiPacket

const val DEFAULT_PART_FRAME_WIDTH = 50
const val DEFAULT_PART_FRAME_HEIGHT = 50

@Serializable
data class PartFrame(
    val width: Int?, //null - default
    val height: Int?, //null - default
    val startX: Int,
    val startY: Int,
    val colors: List<Int>
) : VrFrame

@Serializable
data class DeltaFrame(
    val colors: List<Triple<Int, Int, Int>>
) : VrFrame


@Serializable
data class Speed(
    val speed: Int
) : UiPacket