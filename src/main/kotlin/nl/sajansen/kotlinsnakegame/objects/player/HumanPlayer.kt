package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.multiplayer.json.PlayerDataJson
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.adjustPositionForWall
import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.other.Gnome
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.props.Star
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.sound.SoundPlayer
import nl.sajansen.kotlinsnakegame.objects.sound.Sounds
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger

class HumanPlayer(
    override var name: String = "Player",
    var startPosition: Point? = null
) : Player, MovablePlayer, Gnome(), KeyEventListener {
    private val logger = Logger.getLogger(HumanPlayer::class.java.name)

    var speed = 5
    override var score = 0
        set(value) {
            field = value
            logger.info("$name scores increases to: $value")
        }

    private var previousPosition = Point(0, 0)
    private var pushFood = true
    private var keysPressedUp = false
    private var keysPressedRight = false
    private var keysPressedDown = false
    private var keysPressedLeft = false
    private var keyPresses = arrayListOf<Direction>()

    // Controls
    override var upKey: Int = Config.player1UpKey
    override var rightKey: Int = Config.player1RightKey
    override var downKey: Int = Config.player1DownKey
    override var leftKey: Int = Config.player1LeftKey

    init {
        EventHub.register(this)
    }

    override fun reset() {
        super.reset()

        setRandomStartPosition()

        direction = Direction.NONE
        position = startPosition?.clone() as Point? ?: Point(0, 0)
        score = 0
        Game.board.entities.remove(this)
        Game.board.entities.add(this)
    }

    fun setRandomStartPosition() {
        startPosition = Game.board.getRandomEmptyPoint(size) ?: return
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            upKey -> addDirection(Direction.NORTH)
            rightKey -> addDirection(Direction.EAST)
            downKey -> addDirection(Direction.SOUTH)
            leftKey -> addDirection(Direction.WEST)
            Config.playerPushFood -> pushFood = false
        }

        calculateDirection()
    }

    override fun keyReleased(e: KeyEvent) {
        when (e.keyCode) {
            upKey -> removeDirection(Direction.NORTH)
            rightKey -> removeDirection(Direction.EAST)
            downKey -> removeDirection(Direction.SOUTH)
            leftKey -> removeDirection(Direction.WEST)
            Config.playerPushFood -> pushFood = true
        }

        calculateDirection()
    }

    private fun addDirection(direction: Direction) {
        removeDirection(direction)
        keyPresses.add(direction)
    }

    private fun removeDirection(direction: Direction) {
        keyPresses.remove(direction)
    }

    private fun calculateDirection() {
        direction = if (keyPresses.size == 0) Direction.NONE else keyPresses.last()
    }

    override fun destroy() {
        logger.warning("Cannot destroy player object")
        Game.end("Player is destroyed")
    }

    override fun step() {
        super.step()
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

        adjustPositionForWall(position, size)
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
            Direction.NORTH -> entity.position.y = position.y + hitboxes[0].y - entity.size.height
            Direction.EAST -> entity.position.x = position.x + hitboxes[0].x + hitboxes[0].width
            Direction.SOUTH -> entity.position.y = position.y + hitboxes[0].y + hitboxes[0].height
            Direction.WEST -> entity.position.x = position.x + hitboxes[0].x - entity.size.width
            else -> {
            }
        }
    }

    private fun consume(food: Food) {
        logger.info("Player eats food")
        score += food.points
        food.destroy()
        SoundPlayer.play(Sounds.EAT_FOOD)
    }

    override fun toString(): String {
        return "HumanPlayer(name=$name, score=$score, position=$position)"
    }

    override fun toPlayerDataJson(): PlayerDataJson {
        return PlayerDataJson(
            className = this::class.java.name,
            name = name,
            position = position,
            direction = direction,
        )
    }

    override fun fromPlayerDataJson(data: PlayerDataJson): Player {
        name = data.name
        position = data.position
        direction = data.direction
        return this
    }
}