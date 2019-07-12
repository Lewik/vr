package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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
    private val timerDelayDefault = 100L

    private val robot = Robot()

    fun toggleScreenshooting() {
        if (timerDelay == null) {
            timerDelay = timerDelayDefault
            startScreenshoting()
        } else {
            timerDelay = null
        }
    }

    fun startScreenshoting() {
        println("Screenshoting")
        val screenSize = Toolkit.getDefaultToolkit().screenSize

        val shot = robot.createScreenCapture(Rectangle(0, 0, screenSize.width, screenSize.height))


        val newParts =
            (0 until (screenSize.width - DEFAULT_PART_FRAME_WIDTH) step DEFAULT_PART_FRAME_WIDTH).flatMap { startX ->
                (0 until (screenSize.height - DEFAULT_PART_FRAME_HEIGHT) step DEFAULT_PART_FRAME_HEIGHT).mapNotNull { startY ->
                    getPartFrame(shot, startX, startY)
                }
            }
        println("parts: ${newParts.size}")
        newParts.forEach { sendGateway.send(it) }

        if (timerDelay != null) {
            Timer("", false).schedule(timerDelay!!) { startScreenshoting() }
        }
    }

    private val sentPartFrames = mutableMapOf<Int, MutableMap<Int, List<Int>>>()


    private fun getPartFrame(shot: BufferedImage, startX: Int, startY: Int): NetworkPacket? {
        val colors =
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

        val sentColors = sentPartFrames.getOrPut(startX) { mutableMapOf() }[startY]

        return if (sentColors != colors) {
            //println("new")
            sentPartFrames.getValue(startX)[startY] = colors

            NetworkPacket(
                partFrame = PartFrame(
                    width = null,
                    height = null,
                    colors = colors,
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