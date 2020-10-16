package nl.sajansen.kotlinsnakegame.objects.entities.other


import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.logging.Logger

open class Gnome : Sprite() {
    private val logger = Logger.getLogger(Gnome::class.java.name)

    override var sprite = Sprites.GNOME_NEUTRAL_1
    override var size = Dimension(19, 32)
    override var spriteSpeed = 3
    override var solid = false
    var direction: Direction = Direction.NONE

    override fun step() {
    }

    override fun paint(): BufferedImage {
        sprite = when (direction) {
            Direction.NORTH -> Sprites.GNOME_WALKING_NORTH_1
            Direction.EAST -> Sprites.GNOME_WALKING_EAST_1
            Direction.SOUTH -> Sprites.GNOME_WALKING_SOUTH_1
            Direction.WEST -> Sprites.GNOME_WALKING_WEST_1
            else -> Sprites.GNOME_NEUTRAL_1
        }
        return super.paint()
    }
}