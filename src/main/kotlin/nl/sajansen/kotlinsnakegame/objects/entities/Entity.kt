package nl.sajansen.kotlinsnakegame.objects.entities

import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Dimension
import java.awt.Point

interface Entity {
    var position: Point
    var size: Dimension

    fun reset()
    fun step()
    fun destroy() {
        Game.board.entities.remove(this)
    }
}