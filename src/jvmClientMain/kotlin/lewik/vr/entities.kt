package lewik.vr

import kotlinx.serialization.Serializable

@Serializable
data class Packet(
    val fullFrame: FullFrame? = null,
    val deltaFrame: DeltaFrame? = null
)

interface VrFrame
@Serializable
data class FullFrame(
    val width: Int,
    val height: Int,
    val colors: List<Int>
) : VrFrame

@Serializable
data class DeltaFrame(
    val colors: List<Triple<Int, Int, Int>>
) : VrFrame