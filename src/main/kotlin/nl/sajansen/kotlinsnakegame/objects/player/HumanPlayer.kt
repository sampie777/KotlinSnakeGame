package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger

class HumanPlayer : Player, Sprite(), KeyEventListener {
    private val logger = Logger.getLogger(HumanPlayer::class.java.name)

    var direction: Direction = Direction.NONE
    var speed = 5
    override var name: String = ""
    override var score = 0
        set(value) {
            field = value
            logger.info("Player scores increases to: $value")
        }

    override var sprite = Sprites.PLAYER_FACE_1
    override var solid = false

    init {
        EventHub.register(this)
    }

    override fun reset() {
        position = Point(0, 0)
        score = 0
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            Config.player1UpKey -> direction = Direction.NORTH
            Config.player1RightKey -> direction = Direction.EAST
            Config.player1DownKey -> direction = Direction.SOUTH
            Config.player1LeftKey -> direction = Direction.WEST
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == Config.player1UpKey && direction == Direction.NORTH) {
            direction = Direction.NONE
        } else if (e.keyCode == Config.player1RightKey && direction == Direction.EAST) {
            direction = Direction.NONE
        } else if (e.keyCode == Config.player1DownKey && direction == Direction.SOUTH) {
            direction = Direction.NONE
        } else if (e.keyCode == Config.player1LeftKey && direction == Direction.WEST) {
            direction = Direction.NONE
        }
    }

    override fun destroy() {
        logger.warning("Cannot destroy player object")
        Game.end("Player is destroyed")
    }

    override fun step() {
        val oldPosition = position.clone() as Point

        moveToNewPosition()

        val spritesAtPosition = Game.board.getSpritesAt(this)

        if (spritesAtPosition.any { it.solid }) {
            position = oldPosition
            return
        }

        spritesAtPosition.filterIsInstance<Food>()
            .forEach {
                consume(it)
            }
    }

    private fun moveToNewPosition() {
        when (direction) {
            Direction.NORTH -> position.y -= speed
            Direction.EAST -> position.x += speed
            Direction.SOUTH -> position.y += speed
            Direction.WEST -> position.x -= speed
            else -> {
            }
        }

        if (!Config.playerWarpsThroughWalls) {
            return
        }

        if (position.x + size.width / 2 < 0) {
            position.x = Game.board.size.width - size.width
        } else if (position.x + size.width / 2 > Game.board.size.width) {
            position.x = 0
        }

        if (position.y + size.height / 2 < 0) {
            position.y = Game.board.size.height - size.height
        } else if (position.y + size.height / 2 > Game.board.size.height) {
            position.y = 0
        }
    }

    private fun consume(food: Food) {
        logger.info("Player eats food")
        score += food.points
        food.destroy()
    }
}