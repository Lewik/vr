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
                (0 until DEFAULT_PART_FRAME_WIDTH).forEach { x ->
                    (0 until DEFAULT_PART_FRAME_HEIGHT).forEach { y ->
                        try {
                            graphics.color = Color(packet.colors[i++])
                            graphics.drawLine(
                                packet.startX + x,
                                packet.startY + y,
                                packet.startX + x,
                                packet.startY + y
                            )
                        } catch (e: Throwable) {
                            println("asd")
                        }
                    }
                }


//                graphics.color = Color(
//                    (Math.random() * 255).toInt(),
//                    (Math.random() * 255).toInt(),
//                    (Math.random() * 255).toInt()
//                )
//                graphics.fillRect(
//                    packet.startX - 1,
//                    packet.startY - 1,
//                    50 + 1,
//                    50 + 1
//                )
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