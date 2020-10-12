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

        val restartItem = JMenuItem("Restart")
        val quitItem = JMenuItem("Quit")

        // Set alt keys
        restartItem.mnemonic = KeyEvent.VK_R
        quitItem.mnemonic = KeyEvent.VK_Q
        quitItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK)

        restartItem.addActionListener { Game.restart() }
        quitItem.addActionListener { exitApplication() }

        add(restartItem)
        add(quitItem)
    }
}