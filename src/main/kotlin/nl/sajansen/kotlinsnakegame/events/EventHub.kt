package nl.sajansen.kotlinsnakegame.events

import java.awt.event.KeyEvent
import java.util.logging.Logger

object EventHub : GameEventListener, KeyEventListener {
    private val logger = Logger.getLogger(EventHub::class.java.name)

    private val gameEventListeners = hashSetOf<GameEventListener>()
    private val keyEventListener = hashSetOf<KeyEventListener>()

    fun register(listener: GameEventListener) {
        logger.info("Adding GameEventListener")
        gameEventListeners.add(listener)
    }

    fun register(listener: KeyEventListener) {
        logger.info("Adding KeyEventListener")
        keyEventListener.add(listener)
    }

    /**
     * GameEventListener events
     */

    override fun runningStateChanged() {
        logger.finer("Sending GameEventListener.runningStateChanged event")
        gameEventListeners.toTypedArray().forEach {
            it.runningStateChanged()
        }
    }

    /**
     * KeyEventListener events
     */

    override fun keyTyped(e: KeyEvent) {
        logger.finer("Sending KeyEventListener.keyTyped event")
        keyEventListener.toTypedArray().forEach {
            it.keyTyped(e)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        logger.finer("Sending KeyEventListener.keyPressed event")
        keyEventListener.toTypedArray().forEach {
            it.keyPressed(e)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        logger.finer("Sending KeyEventListener.keyReleased event")
        keyEventListener.toTypedArray().forEach {
            it.keyReleased(e)
        }
    }
}