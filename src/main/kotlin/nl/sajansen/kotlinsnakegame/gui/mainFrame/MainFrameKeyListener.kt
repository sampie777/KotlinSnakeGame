package nl.sajansen.kotlinsnakegame.gui.mainFrame


import nl.sajansen.kotlinsnakegame.events.EventHub
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.logging.Logger

class MainFrameKeyListener : KeyListener {
    private val logger = Logger.getLogger(MainFrameKeyListener::class.java.name)

    override fun keyTyped(e: KeyEvent) {
        EventHub.keyTyped(e)
    }

    override fun keyPressed(e: KeyEvent) {
        EventHub.keyPressed(e)
    }

    override fun keyReleased(e: KeyEvent) {
        EventHub.keyReleased(e)
    }
}