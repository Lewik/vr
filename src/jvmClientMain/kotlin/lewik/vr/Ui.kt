package lewik.vr

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*


class Ui(uiController: UiController) : JFrame() {
    lateinit var drawPanel: JPanel
    lateinit var speed: JTextPane

    init {
        this.preferredSize = Dimension(400, 400)

        val button = JButton("Show")
        button.addActionListener { event ->
            uiController.toggleScreenshooting()
        }

        speed = JTextPane()


        drawPanel = JPanel()
        drawPanel.layout = SpringLayout()
        drawPanel.preferredSize = Dimension(300, 300)

        val container = this.contentPane
        //container.layout = SpringLayout()
        container.add(button, BorderLayout.PAGE_START)
        container.add(drawPanel, BorderLayout.CENTER)
        container.add(speed, BorderLayout.PAGE_END)

        this.pack()
        this.isVisible = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
    }
}