package nl.sajansen.kotlinsnakegame.objects.visuals

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

    fun paint(): BufferedImage {
        if (screens.size == 0) {
            return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        }

        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.windowSize.width, Game.board.windowSize.height)

        currentScreen()?.paint(g)

        g.dispose()
        return bufferedImage
    }

    override fun mouseClicked(e: MouseEvent) {
        currentScreen()?.mouseClicked(e)
    }

    fun show(screen: Screen) {
        screens.add(screen)
    }

    fun close(screen: Screen) {
        screens.remove(screen)
    }
}