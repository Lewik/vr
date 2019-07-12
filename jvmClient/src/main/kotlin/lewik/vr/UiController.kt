package lewik.vr

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.event.MouseEvent

@Service
class UiController @Autowired constructor(
    private val screenshooter: Screenshooter
) {

    var ui: Ui? = null

    fun updateWith(packet: UiPacket) {
        if (ui != null) {
            val check = when (packet) {
                is PartFrame -> {

                    val graphics = ui!!.remoteScreenDrawPanel.graphics
                    var i = 0
                    (0 until DEFAULT_PART_FRAME_WIDTH).forEach { x ->
                        (0 until DEFAULT_PART_FRAME_HEIGHT).forEach { y ->
                            try {
                                val color = packet.colors[i++]
                                if (color != null) {
                                    graphics.color = Color(color)
                                    graphics.drawLine(
                                        packet.startX + x,
                                        packet.startY + y,
                                        packet.startX + x,
                                        packet.startY + y
                                    )
                                }
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
                    ui!!.speed.text = packet.speed.toString()
                }
                is MousePosition -> {
//                    ui!!.remoteMouseDrawPanel.drawRemoteMouse(packet.point)
//                    ui!!.remoteMouseDrawPanel.repaint()
//                    val graphics = ui!!.remoteScreenDrawPanel.graphics
//                    val mousePosition = packet.point
//                        println("paintComponent inner")
//                        graphics.color = Color.BLUE
//                        graphics.fillRect(
//                            mousePosition!!.first,
//                            mousePosition!!.second,
//                            50 + 1,
//                            50 + 1
//                        )
                }
            }
        }
    }

    fun toggleScreenshooting() {
        screenshooter.toggleScreenshooting()
    }


    fun mouseReleased(mouseEvent: MouseEvent) {
        screenshooter.mousePosition = null
//        println("mouseReleased")
    }

    fun mouseEntered(mouseEvent: MouseEvent) {
//        println("mouseEntered")
    }

    fun mouseClicked(mouseEvent: MouseEvent) {
//        println("mouseClicked")
    }

    fun mouseExited(mouseEvent: MouseEvent) {
        screenshooter.mousePosition = null
//        println("mouseExited")
    }

    fun mousePressed(mouseEvent: MouseEvent) {
        screenshooter.mousePosition = mouseEvent.x to mouseEvent.y
//        println("mousePressed")
    }

    fun mouseMoved(mouseEvent: MouseEvent) {
        screenshooter.mousePosition = null
//        println("mouseMoved")
    }

    fun mouseDragged(mouseEvent: MouseEvent) {
        screenshooter.mousePosition = mouseEvent.x to mouseEvent.y
//        println("mouseDragged")
    }
}