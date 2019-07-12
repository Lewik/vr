package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.MouseInfo
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.util.*
import kotlin.concurrent.schedule

@Service
class Screenshooter @Autowired constructor(
    private val sendGateway: SendGateway
) {
    private var timerDelay: Long? = null
    private val timerDelayDefault = 50L

    private val robot = Robot()

    @Volatile
    var mousePosition: Pair<Int, Int>? = null
    var previousMousePosition: Pair<Int, Int>? = null

    fun toggleScreenshooting() {
        if (timerDelay == null) {
            timerDelay = timerDelayDefault
            startScreenshoting()
        } else {
            timerDelay = null
        }
    }

    fun startScreenshoting() {
//        println("Screenshoting")
        val screenSize = Toolkit.getDefaultToolkit().screenSize

        val shot = robot.createScreenCapture(Rectangle(0, 0, screenSize.width / 3, screenSize.height))


        val newParts =
            (0 until (screenSize.width / 3 - DEFAULT_PART_FRAME_WIDTH) step DEFAULT_PART_FRAME_WIDTH).flatMap { startX ->
                (0 until (screenSize.height - DEFAULT_PART_FRAME_HEIGHT) step DEFAULT_PART_FRAME_HEIGHT).mapNotNull { startY ->
                    getPartFrame(shot, startX, startY)
                }
            }
//        println("parts: ${newParts.size}")
        newParts.forEach { sendGateway.send(it) }

        sendGateway.send(NetworkPacket(mousePosition = MousePosition(MouseInfo.getPointerInfo().location.let { it.x to it.y })))

        if (timerDelay != null) {
            Timer("", false).schedule(timerDelay!!) { startScreenshoting() }
        }
    }

    private val sentPartFrames = mutableMapOf<Int, MutableMap<Int, List<Int>>>()


    private fun getPartFrame(shot: BufferedImage, startX: Int, startY: Int): NetworkPacket? {
        val currentColors =
            (startX until ((startX + DEFAULT_PART_FRAME_WIDTH))).flatMap { x ->
                (startY until (startY + DEFAULT_PART_FRAME_HEIGHT)).mapNotNull { y ->
                    try {
                        shot.getRGB(x, y)
                    } catch (e: Throwable) {
                        println("start: $startX $startY pixel: $x $y shotHW: ${shot.width} ${shot.height}")
                        println(e)
                        null
                    }
                }
            }

        val previousColors = sentPartFrames.getOrPut(startX) { mutableMapOf() }[startY]

        return if (previousColors != currentColors) {
            //println("new")
            sentPartFrames.getValue(startX)[startY] = currentColors

            val deltaColors = if (previousColors != null) {
                currentColors.zip(previousColors) { current, previous -> if (current == previous) null else current }
            } else {
                currentColors
            }

            NetworkPacket(
                partFrame = PartFrame(
                    width = null,
                    height = null,
                    colors = deltaColors,
                    startX = startX,
                    startY = startY
                )
            )
        } else {
            //println("old")
            null
        }
    }
}