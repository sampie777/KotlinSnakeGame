package nl.sajansen.kotlinsnakegame.gui.menu

import nl.sajansen.kotlinsnakegame.exitApplication
import nl.sajansen.kotlinsnakegame.gui.config.ConfigFrame
import nl.sajansen.kotlinsnakegame.gui.utils.getMainFrameComponent
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.screens.StartScreen
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
        val mainMenuItem = JMenuItem("Main menu")
        val settingsItem = JMenuItem("Settings")
        val quitItem = JMenuItem("Quit")

        // Set alt keys
        pauseItem.mnemonic = KeyEvent.VK_P
        pauseItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)
        restartItem.mnemonic = KeyEvent.VK_R
        restartItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK)
        mainMenuItem.mnemonic = KeyEvent.VK_M
        mainMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK)
        settingsItem.mnemonic = KeyEvent.VK_S
        settingsItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK.or(InputEvent.ALT_MASK))
        quitItem.mnemonic = KeyEvent.VK_Q
        quitItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK)

        pauseItem.addActionListener { Game.togglePause() }
        restartItem.addActionListener { Game.restart() }
        mainMenuItem.addActionListener {
            Game.stop()
            StartScreen.show()
        }
        settingsItem.addActionListener { ConfigFrame(getMainFrameComponent(this)) }
        quitItem.addActionListener { exitApplication() }

        add(mainMenuItem)
        add(pauseItem)
        add(restartItem)
        addSeparator()
        add(settingsItem)
        add(quitItem)
    }
}