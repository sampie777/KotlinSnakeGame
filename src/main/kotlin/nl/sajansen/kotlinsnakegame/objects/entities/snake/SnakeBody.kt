package nl.sajansen.kotlinsnakegame.objects.entities.snake


import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.colorizeImage
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import java.awt.image.BufferedImage
import java.util.logging.Logger

class SnakeBody(override val snakePlayer: SnakePlayer) : SnakePart, Sprite() {
    private val logger = Logger.getLogger(SnakeBody::class.java.name)

    override var sprite = Sprites.SNAKE_BODY_1

    override fun reset() {
        super.reset()
        destroy()
    }

    override fun step() {
    }

    override fun paint(): BufferedImage {
        return colorizeImage(super.paint(), snakePlayer.color)
    }
}