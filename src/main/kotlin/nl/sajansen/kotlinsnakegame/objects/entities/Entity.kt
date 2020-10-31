package nl.sajansen.kotlinsnakegame.objects.entities

import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle

interface Entity {
    var position: Point
    var size: Dimension
    var hitboxes: ArrayList<Rectangle>

    fun reset()
    fun step()
    fun destroy() {
        Game.board.entities.remove(this)
    }
}