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

    fun updateWith(networkPacket: NetworkPacket) {
        if (networkPacket.partFrame != null) {
            updateWith(networkPacket.partFrame)
        }
    }

    fun updateWith(packet: UiPacket) {
        when (packet) {
            is PartFrame -> {
                val graphics = ui.drawPanel.graphics
                var i = 0
                (0 until packet.width).forEach { x ->
                    (0 until packet.height).forEach { y ->
                        graphics.color = Color(packet.colors[i++])
                        graphics.drawLine(
                            packet.x + x,
                            packet.y + y,
                            packet.x + x,
                            packet.y + y
                        )
                    }
                }
            }
            is Speed -> {
                ui.speed.text = packet.speed.toString()
            }
        }
    }

    fun toggleScreenshooting() {
        screenshooter.toggleScreenshooting()
    }
}