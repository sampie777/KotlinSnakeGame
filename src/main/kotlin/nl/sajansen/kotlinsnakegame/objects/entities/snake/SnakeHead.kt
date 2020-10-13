package nl.sajansen.kotlinsnakegame.objects.entities.snake


import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.colorizeImage
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import java.awt.image.BufferedImage
import java.util.logging.Logger

class SnakeHead(val snakePlayer: SnakePlayer) : Sprite() {
    private val logger = Logger.getLogger(SnakeHead::class.java.name)

    override var sprite = Sprites.SNAKE_HEAD_1

    override fun reset() {
    }

    override fun step() {
    }

    override fun paint(): BufferedImage {
        return colorizeImage(super.paint(), snakePlayer.color)
    }
}