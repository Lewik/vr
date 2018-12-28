package lewik.vr

import kotlinx.serialization.Serializable

@Serializable
data class Packet(
    val partFrame: PartFrame? = null,
    val deltaFrame: DeltaFrame? = null
)

interface VrFrame
@Serializable
data class PartFrame(
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
    val colors: List<Int>
) : VrFrame

@Serializable
data class DeltaFrame(
    val colors: List<Triple<Int, Int, Int>>
) : VrFrame