package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.ConfigEventListener
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.multiplayer.json.PlayerDataJson
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.adjustPositionForWall
import nl.sajansen.kotlinsnakegame.objects.drawShadowedString
import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.props.Star
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeBody
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeHead
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import nl.sajansen.kotlinsnakegame.objects.sound.SoundPlayer
import nl.sajansen.kotlinsnakegame.objects.sound.Sounds
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

class SnakePlayer(
    override var name: String = "Player",
    color: Color = availableColors.first(),
    var startPosition: Point? = null
) : Player, MovablePlayer, KeyEventListener, ConfigEventListener {
    private val logger = Logger.getLogger(SnakePlayer::class.java.name)

    // Just some empty values, see reset() for the real values
    private var updateInterval = 0
    private var nextUpdateTime: Long = 0
    var headEntity = SnakeHead(this)
    private var bodyEntities = arrayListOf<SnakeBody>()

    override var score = 0
        set(value) {
            field = value
            logger.info("$name scores increases to: $value")
        }

    var color: Color = color
        get() {
            if (hasStarEffect()) {
                return when (Random.nextInt(0, 6)) {
                    0 -> Color(255, 255, 200, 255)
                    else -> Color(255, 255, 0, 255)
                }
            }
            return field
        }

    private var direction: Direction = Direction.NONE
    private var nextDirection: Direction = Direction.NONE
    private var speed = Game.board.gridSize
    private var starWearsOutTime = 0L

    // Controls
    override var upKey: Int = Config.player1UpKey
    override var rightKey: Int = Config.player1RightKey
    override var downKey: Int = Config.player1DownKey
    override var leftKey: Int = Config.player1LeftKey

    companion object {
        val availableColors = arrayOf(
            Color(189, 189, 189),
            Color(255, 255, 255),
            Color(0, 255, 0),
            Color(255, 150, 0),
            Color(255, 0, 0),
            Color(255, 0, 200),
            Color(63, 63, 255),
            Color(0, 204, 255),
        )
    }

    init {
        EventHub.register(this)
    }

    override fun reset() {
        direction = if (Config.snakeOnlyLeftRightControls) Direction.SOUTH else Direction.NONE
        nextDirection = direction
        score = 3
        nextUpdateTime = 0
        starWearsOutTime = 0
        boostSpeed(false)
        initEntities()
    }

    private fun initEntities() {
        logger.info("Resetting snake's entities")
        headEntity.destroy()
        bodyEntities.forEach { it.destroy() }

        setRandomStartPosition()

        // Creating new head
        headEntity = SnakeHead(this)
        headEntity.position = startPosition?.clone() as Point? ?: Point(0, 0)
        Game.board.entities.add(headEntity)

        // Creating new body
        bodyEntities.clear()
        while (bodyEntities.size < score) {
            addBodyEntityAt(headEntity.position)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        if (Config.snakeOnlyLeftRightControls) {
            when (e.keyCode) {
                rightKey -> nextDirection = directionForRightTurn()
                leftKey -> nextDirection = directionForLeftTurn()
            }
        } else {
            when (e.keyCode) {
                upKey -> nextDirection = Direction.NORTH
                rightKey -> nextDirection = Direction.EAST
                downKey -> nextDirection = Direction.SOUTH
                leftKey -> nextDirection = Direction.WEST
            }
        }

        if (e.keyCode == Config.snakeBoostKey) {
            boostSpeed(true)
        }
    }

    private fun directionForLeftTurn() = Direction.fromValue((8 + direction.value - 2) % 8) ?: Direction.NONE

    private fun directionForRightTurn() = Direction.fromValue((direction.value + 2) % 8) ?: Direction.NONE

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == Config.snakeBoostKey) {
            boostSpeed(false)
        }
    }

    override fun propertyUpdated(name: String, value: Any?) {
        if (name == "snakeStepInterval") {
            updateInterval = Config.snakeStepInterval
        }
    }

    private fun boostSpeed(enable: Boolean) {
        updateInterval = if (enable) {
            max(1, Config.snakeStepInterval / 3)
        } else {
            Config.snakeStepInterval
        }
    }

    override fun step() {
        if (Game.state.runningState != GameRunningState.STARTED) {
            return
        }

        if (!isItTimeToUpdateMovement()) {
            return
        }

        updateBodyPositions()
        moveToNewPosition()
    }

    private fun isItTimeToUpdateMovement(): Boolean {
        if (nextUpdateTime > Game.state.time) {
            return false
        }
        nextUpdateTime = Game.state.time + updateInterval
        return true
    }

    private fun moveToNewPosition() {
        if (newDirectionIsBackwards()) {
            nextDirection = direction
        }

        direction = nextDirection
        when (direction) {
            Direction.NORTH -> headEntity.position.y -= speed
            Direction.EAST -> headEntity.position.x += speed
            Direction.SOUTH -> headEntity.position.y += speed
            Direction.WEST -> headEntity.position.x -= speed
            else -> {
            }
        }

        if (!Config.playerWarpsThroughWalls) {
            if (Config.snakeCollidesWithWalls && headIsOutOfBoard()) {
                Game.end("$name burst its head against the wall")
                return
            }
        }

        adjustPositionForWall(headEntity.position, headEntity.size, sizeMarginFactor = 0.0)
    }

    private fun newDirectionIsBackwards(): Boolean {
        return (8 + direction.value - nextDirection.value) % 8 == 4
    }

    private fun cutOffBodyAt(body: SnakeBody) {
        val index = bodyEntities.indexOf(body)
        logger.info("Cutting off snake's body at index $index")

        do {
            bodyEntities.first().destroy()
        } while (bodyEntities.removeFirst() != body)

        score -= index + 1
    }

    private fun headIsOutOfBoard(): Boolean {
        return headEntity.position.x < 0 || headEntity.position.x + headEntity.size.width > Game.board.size.width
                || headEntity.position.y < 0 || headEntity.position.y + headEntity.size.height > Game.board.size.height
    }

    fun headCollidedWith(entity: Entity) {
        when (entity) {
            is Food -> return consume(entity)
            is Star -> return catchStar(entity)
            is SnakeHead -> return processCollisionWithSnakeHead(entity)
            is SnakeBody -> return processCollisionWithSnakeBody(entity)
            is HumanPlayer -> return processCollisionWithHumanPlayer(entity)
            !is Sprite -> return
            else -> {
                if (!entity.solid) {
                    return
                }

                logger.info("$this collided with $entity")
                Game.end("$name burst its head")
            }
        }
    }

    private fun processCollisionWithHumanPlayer(player: HumanPlayer) {
        if (!Config.snakeEatsHumanPlayer) {
            return
        }

        Game.end("$name ate ${player.name} alive", Sounds.SNAKE_EAT_GNOME)
    }

    private fun processCollisionWithSnakeHead(snakeHead: SnakeHead) {
        val otherSnake = snakeHead.snakePlayer

        if (hasStarEffect()) {
            if (otherSnake.hasStarEffect()) {
                return Game.end("Big Bang between $name and ${otherSnake.name}!")
            }

            return Game.end("${otherSnake.name} was eaten alive by $name")
        }

        if (otherSnake.hasStarEffect()) {
            return Game.end("$name was eaten alive by ${otherSnake.name}")
        }

        Game.end("Head to head collision between $name and ${otherSnake.name}")
    }

    private fun processCollisionWithSnakeBody(snakeBody: SnakeBody) {
        val otherSnake = snakeBody.snakePlayer

        if (otherSnake == this) {
            if (hasStarEffect()) {
                return
            }

            if (direction == Direction.NONE) {
                logger.fine("Game just started, cannot collide with itself")
                return
            }
            
            if (bodyEntities.size == 1 && bodyEntities.last() == snakeBody) {
                logger.info("It is impossible for $name to run into its body of length 1")
                return
            }

            return Game.end("$name ate itself")
        }

        if (hasStarEffect()) {
            return otherSnake.cutOffBodyAt(snakeBody)
        }

        if (isPossibleHeadToHeadCollisionWith(otherSnake, snakeBody)) {
            return Game.end("${otherSnake.name} was eaten alive by $name")
        }

        Game.end("$name ran into ${otherSnake.name}")
    }

    private fun isPossibleHeadToHeadCollisionWith(otherSnake: SnakePlayer, snakeBody: SnakeBody): Boolean {
        return otherSnake.bodyEntities.isNotEmpty()
                && otherSnake.bodyEntities.indexOf(snakeBody) == otherSnake.bodyEntities.size - 1
                && abs(otherSnake.direction.value - direction.value) == 4
    }

    private fun addBodyEntityAt(point: Point, index: Int? = null) {
        val entity = SnakeBody(this)
        entity.position = point.clone() as Point
        Game.board.entities.add(entity)

        if (index == null) {
            bodyEntities.add(entity)
        } else {
            bodyEntities.add(index, entity)
        }
    }

    private fun updateBodyPositions() {
        if (bodyEntities.isEmpty()) {
            return
        }

        val entityToRemove = bodyEntities.first()
        entityToRemove.destroy()
        bodyEntities.remove(entityToRemove)

        addBodyEntityAt(headEntity.position)
    }

    private fun consume(food: Food) {
        logger.info("Player eats food")
        score += food.points
        food.destroy()
        SoundPlayer.play(Sounds.EAT_FOOD)

        if (bodyEntities.size > 0) {
            addBodyEntityAt(bodyEntities[0].position, 0)
        } else {
            addBodyEntityAt(headEntity.position, 0)
        }
    }

    private fun catchStar(star: Star) {
        logger.info("Player caught star")
        star.destroy()
        starWearsOutTime = Game.state.time + Config.starEffectTime * Config.stepPerSeconds
        logger.info("Player has been given the star effect until time: $starWearsOutTime")
    }

    private fun hasStarEffect() = starWearsOutTime > Game.state.time

    fun setRandomStartPosition() {
        startPosition = Game.board.getRandomEmptyPoint(headEntity.size) ?: return
    }

    override fun toString(): String {
        return "SnakePlayer(name=$name, score=$score, position=${headEntity.position}, color=$color)"
    }

    override fun toPlayerDataJson(): PlayerDataJson {
        return PlayerDataJson(
            className = this::class.java.name,
            name = name,
            position = headEntity.position,
            direction = direction,
            color = color,
        )
    }

    override fun fromPlayerDataJson(data: PlayerDataJson): Player {
        name = data.name
        headEntity.position = data.position
        direction = data.direction
        color = color
        return this
    }

    override fun paintName(g: Graphics2D) {
        val width = g.fontMetrics.stringWidth(name)
        g.color = color
        g.drawShadowedString(name, headEntity.position.x + (headEntity.size.width - width) / 2, headEntity.position.y - 10, 1)
    }
}