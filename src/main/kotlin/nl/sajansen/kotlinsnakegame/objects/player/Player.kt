package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger

class Player : Sprite(), KeyEventListener {
    private val logger = Logger.getLogger(Player::class.java.name)

    var name: String = ""
    var direction: Direction = Direction.NONE
    var speed = 5

    override var sprite = Sprites.PLAYER_FACE_1

    init {
        EventHub.register(this)
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_UP -> direction = Direction.NORTH
            KeyEvent.VK_RIGHT -> direction = Direction.EAST
            KeyEvent.VK_DOWN -> direction = Direction.SOUTH
            KeyEvent.VK_LEFT -> direction = Direction.WEST
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_UP && direction == Direction.NORTH) {
            direction = Direction.NONE
        } else if (e.keyCode == KeyEvent.VK_RIGHT && direction == Direction.EAST) {
            direction = Direction.NONE
        } else if (e.keyCode == KeyEvent.VK_DOWN && direction == Direction.SOUTH) {
            direction = Direction.NONE
        } else if (e.keyCode == KeyEvent.VK_LEFT && direction == Direction.WEST) {
            direction = Direction.NONE
        }
    }

    override fun reset() {
        position = Point(0, 0)
    }

    override fun step() {
        val oldPosition = position.clone() as Point
        when (direction) {
            Direction.NORTH -> position.y -= speed
            Direction.EAST -> position.x += speed
            Direction.SOUTH -> position.y += speed
            Direction.WEST -> position.x -= speed
            else -> {
            }
        }

        if (Game.board.isSolidObstacleAt(this)) {
            position = oldPosition
            return
        }
    }
}