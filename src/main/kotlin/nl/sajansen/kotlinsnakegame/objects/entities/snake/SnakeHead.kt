package nl.sajansen.kotlinsnakegame.objects.entities.snake


import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import java.util.logging.Logger

class SnakeHead : Sprite() {
    private val logger = Logger.getLogger(SnakeHead::class.java.name)

    override var sprite = Sprites.SNAKE_HEAD_1

    override fun reset() {
    }

    override fun step() {
    }
}