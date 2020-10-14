package nl.sajansen.kotlinsnakegame.objects.entities.props


import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Dimension
import java.awt.Point
import java.util.logging.Logger

class Food(
    override var position: Point = Point(0, 0)
) : Sprite() {
    private val logger = Logger.getLogger(Food::class.java.name)

    override var size = Dimension(16, 16)
    override var sprite = Sprites.FOOD_1
    override var solid = false
    var points = 1

    override fun reset() {
    }

    override fun step() {
    }

    override fun destroy() {
        super.destroy()
        Game.board.spawnRandomFood()
    }
}