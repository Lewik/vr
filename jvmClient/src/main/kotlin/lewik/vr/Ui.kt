package lewik.vr

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.*


class Ui(
    uiController: UiController
) : JFrame() {
    var tabbedPane: JTabbedPane
    var remoteScreenDrawPanel: JPanel
    var remoteMouseDrawPanel: RemoteMouseDrawPanel
    var speed: JTextPane
    var sendUuid: JTextPane
    var receiveUuid: JTextPane
    var startStopSendButton: JButton
    var startStopReceiveButton: JButton

    init {
        preferredSize = Dimension(400, 400)

        val test = UUID.randomUUID().toString()
        sendUuid = JTextPane().also { it.text = test }
        receiveUuid = JTextPane().also { it.text = test }

        startStopSendButton = JButton("Start send")
        startStopSendButton.addActionListener {
            uiController.toggleScreenshooting(sendUuid.text)
            startStopSendButton.text = if (startStopSendButton.text == "Start send") {
                "Stop send"
            } else {
                "Start send"
            }
        }

        startStopReceiveButton = JButton("Start receive")
        startStopReceiveButton.addActionListener {
            uiController.toggleReceiving(receiveUuid.text)
            startStopReceiveButton.text = if (startStopReceiveButton.text == "Start receive") {
                "Stop receive"
            } else {
                "Start receive"
            }
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


        val receivePanel = JPanel().also {
            it.layout = BorderLayout()
            it.add(startStopReceiveButton, BorderLayout.PAGE_START)
            it.add(remoteScreenDrawPanel, BorderLayout.CENTER)
            it.add(receiveUuid, BorderLayout.PAGE_END)
        }

        val sendPanel = JPanel().also {
            it.layout = BorderLayout()
            it.add(startStopSendButton, BorderLayout.PAGE_START)
            it.add(sendUuid, BorderLayout.PAGE_END)
        }

        tabbedPane = JTabbedPane().also {
            it.addTab("Send", sendPanel)
            it.addTab("Receive", receivePanel)
        }

        this.contentPane.add(tabbedPane)


        this.pack()
        this.isVisible = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
    }
}
