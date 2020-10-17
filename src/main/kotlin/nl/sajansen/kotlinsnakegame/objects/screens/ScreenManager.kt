package nl.sajansen.kotlinsnakegame.objects.screens

import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.MouseEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

object ScreenManager : MouseEventListener {
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

    override fun mouseClicked(e: MouseEvent) {
        currentScreen()?.mouseClicked(e)
    }

    fun show(screen: Screen) {
        logger.info("Opening screen ${screen.javaClass.name}")
        screens.add(screen)
    }

    fun close(screen: Screen) {
        logger.info("Closing screen ${screen.javaClass.name}")
        screens.remove(screen)
    }

    fun closeAll() {
        logger.info("Closing all screens")
        screens.clear()
    }
}