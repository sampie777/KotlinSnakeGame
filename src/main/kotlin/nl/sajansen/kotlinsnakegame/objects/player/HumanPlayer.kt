package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.other.HumanProfile
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.props.Star
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger

class HumanPlayer : Player, HumanProfile(), KeyEventListener {
    private val logger = Logger.getLogger(HumanPlayer::class.java.name)

    var direction: Direction = Direction.NONE
    var speed = 5
    override var name: String = ""
    override var score = 0
        set(value) {
            field = value
            logger.info("Player scores increases to: $value")
        }

    private var previousPosition = Point(0, 0)
    private var pushFood = false

    init {
        EventHub.register(this)
    }

    override fun reset() {
        position = Point(0, 0)
        score = 0
        Game.board.entities.remove(this)
        Game.board.entities.add(this)
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            Config.player1UpKey -> direction = Direction.NORTH
            Config.player1RightKey -> direction = Direction.EAST
            Config.player1DownKey -> direction = Direction.SOUTH
            Config.player1LeftKey -> direction = Direction.WEST
            Config.playerPushFood -> pushFood = true
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
        } else if (e.keyCode == Config.playerPushFood) {
            pushFood = false
        }
    }

    override fun destroy() {
        logger.warning("Cannot destroy player object")
        Game.end("Player is destroyed")
    }

    override fun step() {
        previousPosition = position.clone() as Point

        moveToNewPosition()
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
            position.x = Game.board.size.width - size.width / 2
        } else if (position.x + size.width / 2 > Game.board.size.width) {
            position.x = -size.width / 2
        }

        if (position.y + size.height / 2 < 0) {
            position.y = Game.board.size.height - size.height / 2
        } else if (position.y + size.height / 2 > Game.board.size.height) {
            position.y = -size.height / 2
        }
    }

    override fun collidedWith(entity: Entity) {
        when (entity) {
            is Food -> return handleFood(entity)
            is Star -> return moveStar(entity)
            !is Sprite -> return
            else -> {
                if (!entity.solid) {
                    return
                }

                logger.info("$this collided with $entity")
                position = previousPosition.clone() as Point
            }
        }
    }

    private fun handleFood(food: Food) {
        if (!pushFood) {
            return consume(food)
        }
        pushEntity(food)
    }

    private fun moveStar(star: Star) {
        pushEntity(star)
    }

    private fun pushEntity(entity: Entity) {
        when (direction) {
            Direction.NORTH -> entity.position.y -= speed
            Direction.EAST -> entity.position.x += speed
            Direction.SOUTH -> entity.position.y += speed
            Direction.WEST -> entity.position.x -= speed
            else -> {
            }
        }
    }

    private fun consume(food: Food) {
        logger.info("Player eats food")
        score += food.points
        food.destroy()
    }

    override fun toString(): String {
        return "HumanPlayer(name=$name, score=$score)"
    }
}