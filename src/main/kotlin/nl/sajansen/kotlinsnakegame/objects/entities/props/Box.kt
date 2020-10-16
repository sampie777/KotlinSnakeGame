package nl.sajansen.kotlinsnakegame.objects.entities.props


import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import java.awt.Point
import java.util.logging.Logger

class Box(
    override var position: Point = Point(0, 0),
    override var solid: Boolean = true
) : Sprite() {
    private val logger = Logger.getLogger(Box::class.java.name)

    override var sprite = Sprites.BOX_1

    override fun step() {
    }
}