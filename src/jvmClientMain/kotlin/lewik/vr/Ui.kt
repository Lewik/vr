package lewik.vr

import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JFrame


class Ui(uiController: UiController) : JFrame() {
    init {
        this.preferredSize = Dimension(400, 400)
        this.pack()

        val button = JButton("Show")
        button.addActionListener { event ->
            uiController.toggleScreenshooting()
        }
        this.add(button)


        this.isVisible = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
    }
}