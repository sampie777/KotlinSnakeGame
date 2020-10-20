package nl.sajansen.kotlinsnakegame.gui.mainFrame

import nl.sajansen.kotlinsnakegame.ApplicationInfo
import nl.sajansen.kotlinsnakegame.gui.menu.MenuBar
import java.util.logging.Logger
import javax.swing.JFrame

class MainFrame : JFrame() {
    private val logger = Logger.getLogger(MainFrame::class.java.name)

    companion object {
        private var instance: MainFrame? = null
        fun getInstance() = instance

        fun create(): MainFrame = MainFrame()

        fun createAndShow(): MainFrame {
            val frame = create()
            show()
            return frame
        }

        fun show() {
            getInstance()?.isVisible = true
        }
    }

    init {
        instance = this

        initGUI()

        addKeyListener(MainFrameKeyListener())
    }

    private fun initGUI() {
        add(MainFramePanel())

        jMenuBar = MenuBar()
        title = ApplicationInfo.name
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        pack()
    }
}