package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.awt.MouseInfo
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

@Service
class Screenshooter @Autowired constructor(
    private val sendGateway: SendGateway
) {

    private var timerDelay: Long? = null

    @Value("\${frame_delay}")
    private var timerDelaySetting: Int? = null

    private val robot = Robot()

    private var sendUuid: String = ""

    @Volatile
    var mousePosition: Pair<Int, Int>? = null

    fun toggleScreenshooting(sendUuid: String) {
        if (timerDelay == null) {
            this.sendUuid = sendUuid
            timerDelay = timerDelaySetting!!.toLong()
            startScreenshoting()
        } else {
            timerDelay = null
        }
    }

    fun startScreenshoting() {
//        println("Screenshoting")
        val screenSize = Toolkit.getDefaultToolkit().screenSize

        val screenWidth = screenSize.width
        val screenHeight = screenSize.height
        val shot = robot.createScreenCapture(Rectangle(0, 0, screenWidth, screenHeight))

        val time = Instant.now().toEpochMilli()
        val newParts =
            (0 until (screenWidth - DEFAULT_PART_FRAME_WIDTH) step DEFAULT_PART_FRAME_WIDTH).flatMap { startX ->
                (0 until (screenHeight - DEFAULT_PART_FRAME_HEIGHT) step DEFAULT_PART_FRAME_HEIGHT).mapNotNull { startY ->
                    getPartFrame(shot, startX, startY, time)
                }
            }
        //println("parts: ${newParts.size}, ${newParts.map { it.partFrame!!.createTime }.max()}")
        val mousePosition = MousePosition(
            sessionUuid = this.sendUuid,
            createTime = time,
            point = MouseInfo.getPointerInfo().location.let { it.x to it.y }
        )

        sendGateway.send(
            NetworkPacket(
                sessionUpdate = SessionUpdate(
                    partFrames = newParts,
                    mousePosition = mousePosition,
                    sessionLastUpdate = time
                )
            )
        )

        if (timerDelay != null) {
            Timer("", false).schedule(timerDelay!!) { startScreenshoting() }
        }
    }

    private val sentPartFrames = mutableMapOf<Int, MutableMap<Int, List<Int>>>()


    private fun getPartFrame(
        shot: BufferedImage,
        startX: Int,
        startY: Int,
        time: Long
    ): PartFrame? {
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

            PartFrame(
                sessionUuid = this.sendUuid,
                createTime = time,
                width = null,
                height = null,
                colors = deltaColors,
                startX = startX,
                startY = startY
            )
        } else {
            //println("old")
            null
        }
    }
}
