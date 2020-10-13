package nl.sajansen.kotlinsnakegame.events

import java.awt.event.KeyEvent
import java.util.logging.Logger

object EventHub : GameEventListener, KeyEventListener, ConfigEventListener {
    private val logger = Logger.getLogger(EventHub::class.java.name)

    private val gameEventListeners = hashSetOf<GameEventListener>()
    private val keyEventListener = hashSetOf<KeyEventListener>()
    private val configEventListener = hashSetOf<ConfigEventListener>()

    fun register(listener: EventListener) {
        logger.info("Adding EventListener: ${listener.javaClass.name}")

        if (listener is GameEventListener) {
            logger.info("Adding GameEventListener")
            gameEventListeners.add(listener)
        }

        if (listener is KeyEventListener) {
            logger.info("Adding KeyEventListener")
            keyEventListener.add(listener)
        }

        if (listener is ConfigEventListener) {
            logger.info("Adding ConfigEventListener")
            configEventListener.add(listener)
        }
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

    /**
     * ConfigEventListener events
     */

    override fun propertyUpdated(name: String, value: Any?) {
        logger.finer("Sending ConfigEventListener.propertyUpdated event")
        configEventListener.toTypedArray().forEach {
            it.propertyUpdated(name, value)
        }
    }
}