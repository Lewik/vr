package lewik.vr

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SpringLayout


class Ui(uiController: UiController) : JFrame() {
    lateinit var drawPanel: JPanel

    init {
        this.preferredSize = Dimension(400, 400)

        val button = JButton("Show")
        button.addActionListener { event ->
            uiController.toggleScreenshooting()
        }

        drawPanel = JPanel()
        drawPanel.layout = SpringLayout()
        drawPanel.preferredSize = Dimension(300, 300)

        val container = this.contentPane
        //container.layout = SpringLayout()
        container.add(button, BorderLayout.PAGE_START)
        container.add(drawPanel, BorderLayout.CENTER)

        this.pack()
        this.isVisible = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
    }
}