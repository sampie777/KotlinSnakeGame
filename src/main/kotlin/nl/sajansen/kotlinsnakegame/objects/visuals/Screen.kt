package nl.sajansen.kotlinsnakegame.objects.visuals

import nl.sajansen.kotlinsnakegame.events.MouseEventListener
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.isPointInArea
import nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents.ClickableComponent
import nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents.DrawableComponent
import java.awt.Graphics2D
import java.awt.event.MouseEvent

abstract class Screen : MouseEventListener {
    open var components: ArrayList<DrawableComponent> = arrayListOf()
    open var backgroundOpacity = 255

    fun show() {
        println("Opening ${this.javaClass.name}")
        ScreenManager.show(this)
    }

    fun close() {
        println("Closing ${this.javaClass.name}")
        ScreenManager.close(this)
    }

    open fun paint(g: Graphics2D) {
        paintBackground(g, backgroundOpacity)

        components.forEach {
            when {
                it.componentAlignmentX == ComponentAlignment.CENTER ->
                    it.position.x = (Game.board.windowSize.width - it.size.width) / 2
                it.componentAlignmentY == ComponentAlignment.CENTER ->
                    it.position.y = (Game.board.windowSize.height - it.size.height) / 2
            }

            g.drawImage(it.paint(), null, it.position.x, it.position.y)
        }
    }

    override fun mouseClicked(e: MouseEvent) {
        if (ScreenManager.currentScreen() != this) {
            return
        }
        components.filterIsInstance<ClickableComponent>()
            .filter { isPointInArea(e.point, it.position, it.size) }
            .forEach { it.click(e) }
    }
}
