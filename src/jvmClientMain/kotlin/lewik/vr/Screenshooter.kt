package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.util.*
import kotlin.concurrent.schedule

@Service
class Screenshooter @Autowired constructor(
    private val sendGateway: SendGateway
) {
    private var timerDelay: Long? = null
    private val timerDelayDefault = 1000L

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

        val frameWidth = 50
        val frameHeight = 50

        for (x in 0..screenSize.width step 50) {
            for (y in 0..screenSize.height step 50) {
                sendFrame(x, y, frameWidth, frameHeight)
            }
        }


        if (timerDelay != null) {
            Timer("", false).schedule(timerDelay!!) { startScreenshoting() }
        }
    }

    private fun sendFrame(x: Int, y: Int, frameWidth: Int, frameHeight: Int) {
        val shot = robot.createScreenCapture(Rectangle(x, y, frameWidth, frameHeight))
        val colors = mutableListOf<Int>()
        (0 until shot.width).forEach { x ->
            (0 until shot.height).forEach { y ->
                val color = shot.getRGB(x, y)
                colors.add(color)
            }
        }
        sendGateway.send(
            NetworkPacket(
                partFrame = PartFrame(
                    width = shot.width,
                    height = shot.height,
                    colors = colors,
                    x = x,
                    y = y
                )
            )
        )
    }
}