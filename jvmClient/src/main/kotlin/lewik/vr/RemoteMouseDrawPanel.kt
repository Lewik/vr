package lewik.vr

import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel


class RemoteMouseDrawPanel : JPanel() {
    private var mousePosition: Pair<Int, Int>? = null
    override fun paintComponent(g: Graphics) {
//        super.paintComponent(g)
        println("paintComponent")
        if (mousePosition != null) {
            println("paintComponent inner")
            graphics.color = Color.BLUE
            graphics.fillRect(
                mousePosition!!.first,
                mousePosition!!.second,
                50 + 1,
                50 + 1
            )
        }
    }

    fun drawRemoteMouse(mousePosition: Pair<Int, Int>?) {
        if (this.mousePosition != mousePosition) {
            println("drawRemoteMouse")
            this.mousePosition = mousePosition
        }
    }

}