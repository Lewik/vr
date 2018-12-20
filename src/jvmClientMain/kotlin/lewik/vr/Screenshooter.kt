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
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val shot = robot.createScreenCapture(Rectangle(screenSize))
        val colors = mutableListOf<Int>()
        (0 until shot.width).forEach { x ->
            (0 until shot.height).forEach { y ->
                val color = shot.getRGB(x, y)
                colors.add(color)
            }
        }
        sendGateway.send(
            Packet(
                fullFrame = FullFrame(
                    width = shot.width,
                    height = shot.height,
                    colors = colors
                )
            )
        )

        if (timerDelay != null) {
            Timer("", false).schedule(timerDelay!!) { startScreenshoting() }
        }
    }
}