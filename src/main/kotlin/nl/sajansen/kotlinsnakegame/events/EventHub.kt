package nl.sajansen.kotlinsnakegame.events

import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.logging.Logger

object EventHub : GameEventListener, KeyEventListener, ConfigEventListener, MouseEventListener, ApplicationEventListener {
    private val logger = Logger.getLogger(EventHub::class.java.name)

    private val gameEventListeners = hashSetOf<GameEventListener>()
    private val keyEventListeners = hashSetOf<KeyEventListener>()
    private val configEventListeners = hashSetOf<ConfigEventListener>()
    private val mouseEventListeners = hashSetOf<MouseEventListener>()
    private val applicationEventListeners = hashSetOf<ApplicationEventListener>()

    fun register(listener: EventListener) {
        logger.info("Adding EventListener: $listener (${listener.javaClass.name})")

        if (listener is GameEventListener) {
            logger.info("Adding GameEventListener")
            gameEventListeners.add(listener)
        }

        if (listener is KeyEventListener) {
            logger.info("Adding KeyEventListener")
            keyEventListeners.add(listener)
        }

        if (listener is ConfigEventListener) {
            logger.info("Adding ConfigEventListener")
            configEventListeners.add(listener)
        }

        if (listener is MouseEventListener) {
            logger.info("Adding MouseEventListener")
            mouseEventListeners.add(listener)
        }

        if (listener is ApplicationEventListener) {
            logger.info("Adding ApplicationEventListener")
            applicationEventListeners.add(listener)
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
        keyEventListeners.toTypedArray().forEach {
            it.keyTyped(e)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        logger.finer("Sending KeyEventListener.keyPressed event")
        keyEventListeners.toTypedArray().forEach {
            it.keyPressed(e)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        logger.finer("Sending KeyEventListener.keyReleased event")
        keyEventListeners.toTypedArray().forEach {
            it.keyReleased(e)
        }
    }

    /**
     * ConfigEventListener events
     */

    override fun propertyUpdated(name: String, value: Any?) {
        logger.finer("Sending ConfigEventListener.propertyUpdated event")
        configEventListeners.toTypedArray().forEach {
            it.propertyUpdated(name, value)
        }
    }

    /**
     * MouseEventListener events
     */

    override fun mouseClicked(e: MouseEvent) {
        logger.finer("Sending MouseEventListener.mouseClicked event")
        mouseEventListeners.toTypedArray().forEach {
            it.mouseClicked(e)
        }
    }

    override fun mousePressed(e: MouseEvent) {
        logger.finer("Sending MouseEventListener.mousePressed event")
        mouseEventListeners.toTypedArray().forEach {
            it.mousePressed(e)
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        logger.finer("Sending MouseEventListener.mouseReleased event")
        mouseEventListeners.toTypedArray().forEach {
            it.mouseReleased(e)
        }
    }

    override fun mouseEnteredScreen(e: MouseEvent) {
        logger.finer("Sending MouseEventListener.mouseEnteredScreen event")
        mouseEventListeners.toTypedArray().forEach {
            it.mouseEnteredScreen(e)
        }
    }

    override fun mouseExitedScreen(e: MouseEvent) {
        logger.finer("Sending MouseEventListener.mouseExitedScreen event")
        mouseEventListeners.toTypedArray().forEach {
            it.mouseExitedScreen(e)
        }
    }

    /**
     * ApplicationEventListener events
     */
    override fun onShutDown() {
        logger.finer("Sending ApplicationEventListener.onShutDown event")
        applicationEventListeners.toTypedArray().forEach {
            it.onShutDown()
        }
    }
}