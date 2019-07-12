package lewik.vr

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.*


class Ui(
    uiController: UiController
) : JFrame() {
    lateinit var remoteScreenDrawPanel: JPanel
    lateinit var remoteMouseDrawPanel: RemoteMouseDrawPanel
    lateinit var speed: JTextPane

    init {
        preferredSize = Dimension(400, 400)

        val button = JButton("Show")
        button.addActionListener { event ->
            uiController.toggleScreenshooting()
        }

        speed = JTextPane()


        remoteScreenDrawPanel = JPanel().apply {
            preferredSize = Dimension(300, 300)
            layout = SpringLayout()
            addMouseListener(object : MouseListener {
                override fun mouseReleased(mouseEvent: MouseEvent) = uiController.mouseReleased(mouseEvent)
                override fun mouseEntered(mouseEvent: MouseEvent) = uiController.mouseEntered(mouseEvent)
                override fun mouseClicked(mouseEvent: MouseEvent) = uiController.mouseClicked(mouseEvent)
                override fun mouseExited(mouseEvent: MouseEvent) = uiController.mouseExited(mouseEvent)
                override fun mousePressed(mouseEvent: MouseEvent) = uiController.mousePressed(mouseEvent)
            })
        }

        remoteMouseDrawPanel = RemoteMouseDrawPanel().apply {
//            isOpaque = false
            preferredSize = Dimension(300, 300)
            layout = SpringLayout()
//            location = remoteScreenDrawPanel.location

            addMouseMotionListener(object : MouseMotionListener {
                override fun mouseMoved(mouseEvent: MouseEvent) = uiController.mouseMoved(mouseEvent)
                override fun mouseDragged(mouseEvent: MouseEvent) = uiController.mouseDragged(mouseEvent)
            })
        }


        val container = this.contentPane
//        container.layout = SpringLayout()
        container.add(button, BorderLayout.PAGE_START)
        container.add(remoteScreenDrawPanel, BorderLayout.CENTER)
//        container.add(remoteMouseDrawPanel, BorderLayout.CENTER)
        container.add(speed, BorderLayout.PAGE_END)

        this.pack()
        this.isVisible = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
    }
}