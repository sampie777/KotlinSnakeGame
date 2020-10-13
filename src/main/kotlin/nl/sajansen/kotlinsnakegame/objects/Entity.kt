package nl.sajansen.kotlinsnakegame.objects

import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.image.BufferedImage

interface Entity {
    fun reset()
    fun step()
    fun paint(): BufferedImage
    fun destroy() {
        Game.board.entities.remove(this)
    }
}