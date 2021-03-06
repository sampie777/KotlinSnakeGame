package nl.sajansen.kotlinsnakegame.objects.screens

import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.events.MouseEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.isPointInArea
import java.awt.Graphics2D
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

object ScreenManager : MouseEventListener, KeyEventListener {
    private val logger = Logger.getLogger(ScreenManager::class.java.name)

    private var screens: ArrayList<Screen> = arrayListOf()

    init {
        EventHub.register(this)
    }

    fun currentScreen(): Screen? {
        return screens.lastOrNull()
    }

    fun previousScreen(screen: Screen): Screen? {
        val currentIndex = screens.indexOf(screen)
        return screens.getOrNull(currentIndex - 1)
    }

    fun paint(): BufferedImage {
        if (screens.size == 0) {
            return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        }

        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.windowSize.width, Game.board.windowSize.height)

        paintScreen(g, currentScreen())

        g.dispose()
        return bufferedImage
    }

    private fun paintScreen(g: Graphics2D, screen: Screen?) {
        if (screen == null) {
            return
        }

        if (screen.paintAsOverlay) {
            paintScreen(g, previousScreen(screen))
        }

        screen.paint(g)
    }

    fun show(screen: Screen, index: Int? = null) {
        if (index == null || index >= screens.size) {
            logger.info("Showing screen ${screen.javaClass.name}")
            screens.add(screen)
        } else {
            logger.info("Showing screen ${screen.javaClass.name} at index $index")
            screens.add(index, screen)
        }
    }

    fun close(screen: Screen) {
        logger.info("Closing screen ${screen.javaClass.name}")
        screens.remove(screen)
    }

    fun closeAll() {
        logger.info("Closing all screens")
        screens.clear()
    }

    override fun mouseClicked(e: MouseEvent) {
        currentScreen().let { screen ->
            if (screen is MouseEventListener) {
                screen.mouseClicked(e)
            }

            screen!!.components
                .filter { it is FocusListener }
                .forEach {
                    if (isPointInArea(e.point, it.position, it.size)) {
                        (it as FocusListener).focusGained(null)
                    } else {
                        (it as FocusListener).focusLost(null)
                    }
                }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        currentScreen().let { screen ->
            if (screen is KeyEventListener) {
                logger.info("Forwarding keyReleased event to current screen $screen")
                screen.keyReleased(e)
            }

            screen!!.components.forEach { component ->
                if (component !is KeyEventListener) {
                    return@forEach
                }
                component.keyReleased(e)
            }
        }
    }

    override fun keyTyped(e: KeyEvent) {
        currentScreen().let { screen ->
            if (screen is KeyEventListener) {
                logger.info("Forwarding keyTyped event to current screen $screen")
                screen.keyTyped(e)
            }

            screen!!.components.forEach { component ->
                if (component !is KeyEventListener) {
                    return@forEach
                }
                component.keyTyped(e)
            }
        }
    }

    override fun keyPressed(e: KeyEvent) {
        currentScreen().let { screen ->
            if (screen is KeyEventListener) {
                logger.info("Forwarding keyPressed event to current screen $screen")
                screen.keyPressed(e)
            }

            screen!!.components.forEach { component ->
                if (component !is KeyEventListener) {
                    return@forEach
                }
                component.keyPressed(e)
            }
        }
    }
}