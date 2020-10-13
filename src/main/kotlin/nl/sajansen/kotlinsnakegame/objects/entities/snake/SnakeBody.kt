package nl.sajansen.kotlinsnakegame.objects.entities.snake


import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import java.util.logging.Logger

class SnakeBody : Sprite() {
    private val logger = Logger.getLogger(SnakeBody::class.java.name)

    override var sprite = Sprites.SNAKE_BODY_1

    override fun reset() {
        destroy()
    }

    override fun step() {
    }
}