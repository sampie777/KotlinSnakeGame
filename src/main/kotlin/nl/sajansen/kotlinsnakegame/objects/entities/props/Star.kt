package nl.sajansen.kotlinsnakegame.objects.entities.props


import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import java.awt.Dimension
import java.awt.Point
import java.util.logging.Logger

class Star(
    override var position: Point = Point(0, 0),
    override var solid: Boolean = false
) : Sprite() {
    private val logger = Logger.getLogger(Star::class.java.name)

    override var sprite = Sprites.STAR_2
    override var size = Dimension(32, 30)

    override fun reset() {
    }

    override fun step() {
    }
}