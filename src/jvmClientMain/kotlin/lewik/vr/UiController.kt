package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.Color
import java.util.*
import kotlin.concurrent.schedule

@Service
class UiController @Autowired constructor(
    private val screenshooter: Screenshooter
) {

    private lateinit var ui: Ui

    init {
        val that = this
        Timer("", false).schedule(1000) { ui = Ui(that) }
    }

    fun updateWith(packet: Packet) {
        val graphics = ui.graphics

        if (packet.fullFrame != null) {
            val frame = packet.fullFrame
            var i = 0
            (0 until frame.width).forEach { x ->
                (0 until frame.height).forEach { y ->
                    graphics.color = Color(frame.colors[i++])
                    graphics.drawLine(x, y, x, y)
                }
            }
        }
    }

    fun toggleScreenshooting() {
        screenshooter.toggleScreenshooting()
    }
}