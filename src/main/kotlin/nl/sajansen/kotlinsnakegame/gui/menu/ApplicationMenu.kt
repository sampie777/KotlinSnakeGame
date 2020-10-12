package nl.sajansen.kotlinsnakegame.gui.menu

import nl.sajansen.kotlinsnakegame.exitApplication
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.logging.Logger
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class ApplicationMenu : JMenu("Application") {
    private val logger = Logger.getLogger(ApplicationMenu::class.java.name)

    init {
        initGui()
    }

    private fun initGui() {
        mnemonic = KeyEvent.VK_A

        val pauseItem = JMenuItem("Pause")
        val restartItem = JMenuItem("Restart")
        val quitItem = JMenuItem("Quit")

        // Set alt keys
        pauseItem.mnemonic = KeyEvent.VK_P
        pauseItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)
        restartItem.mnemonic = KeyEvent.VK_R
        restartItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK)
        quitItem.mnemonic = KeyEvent.VK_Q
        quitItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK)

        pauseItem.addActionListener { Game.pause() }
        restartItem.addActionListener { Game.restart() }
        quitItem.addActionListener { exitApplication() }

        add(pauseItem)
        add(restartItem)
        add(quitItem)
    }
}