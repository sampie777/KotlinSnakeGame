package nl.sajansen.kotlinsnakegame.gui.mainFrame


import nl.sajansen.kotlinsnakegame.events.EventHub
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.logging.Logger

class MainFramePanelMouseListener : MouseListener {
    private val logger = Logger.getLogger(MainFramePanelMouseListener::class.java.name)
    override fun mouseClicked(e: MouseEvent) {
        EventHub.mouseClicked(e)
    }

    override fun mousePressed(e: MouseEvent) {
        EventHub.mousePressed(e)
    }

    override fun mouseReleased(e: MouseEvent) {
        EventHub.mouseReleased(e)
    }

    override fun mouseEntered(e: MouseEvent) {
        EventHub.mouseEnteredScreen(e)
    }

    override fun mouseExited(e: MouseEvent) {
        EventHub.mouseExitedScreen(e)
    }
}