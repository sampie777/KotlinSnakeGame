package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.ConfigEventListener
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.props.Star
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeBody
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeHead
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakePart
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import java.awt.Color
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger
import kotlin.math.max
import kotlin.random.Random

class SnakePlayer(
    override var name: String = "Player",
    color: Color = Color(0, 0, 0, 0),
    var startPosition: Point? = null
) : Player, KeyEventListener, ConfigEventListener {
    private val logger = Logger.getLogger(SnakePlayer::class.java.name)

    // Just some empty values, see reset() for the real values
    private var updateInterval = 0
    private var nextUpdateTime: Long = 0
    var headEntity = SnakeHead(this)
    private var bodyEntities = arrayListOf<SnakeBody>()
    override var score = 0
        set(value) {
            field = value
            logger.info("Player scores increases to: $value")
        }
    var color: Color = color
        get() {
            if (hasStarEffect()) {
                return when (Random.nextInt(0, 6)) {
                    0 -> Color(255, 255, 150, 255)
                    else -> Color(255, 255, 0, 255)
                }
            }
            return field
        }

    private var direction: Direction = Direction.NONE
    private var speed = Game.board.gridSize
    private var starWearsOutTime = 0L

    // Controls
    private var upKey: Int = Config.player1UpKey
    private var rightKey: Int = Config.player1RightKey
    private var downKey: Int = Config.player1DownKey
    private var leftKey: Int = Config.player1LeftKey

    init {
        EventHub.register(this)
    }

    override fun reset() {
        direction = if (Config.snakeOnlyLeftRightControls) Direction.SOUTH else Direction.NONE
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
                rightKey -> direction = directionForRightTurn()
                leftKey -> direction = directionForLeftTurn()
            }
        } else {
            when (e.keyCode) {
                upKey -> direction = Direction.NORTH
                rightKey -> direction = Direction.EAST
                downKey -> direction = Direction.SOUTH
                leftKey -> direction = Direction.WEST
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

    override fun destroy() {
        logger.warning("Cannot destroy snake player object")
        Game.end("Player is destroyed")
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

        val spritesAtPosition = Game.board.getSpritesAt(headEntity)
        if (checkForCollision(spritesAtPosition)) {
            return
        }

        processFoodAtPosition(spritesAtPosition)

        processStarAtPosition(spritesAtPosition)
    }

    private fun processFoodAtPosition(spritesAtPosition: List<Sprite>) {
        spritesAtPosition.filterIsInstance<Food>()
            .forEach {
                consume(it)
                addBodyEntityAt(bodyEntities[0].position, 0)

                Game.board.spawnRandomFood()
            }
    }

    private fun processStarAtPosition(spritesAtPosition: List<Sprite>) {
        spritesAtPosition.filterIsInstance<Star>()
            .forEach {
                catchStar(it)

                Game.board.spawnRandomStar()
            }
    }

    private fun isItTimeToUpdateMovement(): Boolean {
        if (nextUpdateTime > Game.state.time) {
            return false
        }
        nextUpdateTime = Game.state.time + updateInterval
        return true
    }

    private fun moveToNewPosition() {
        when (direction) {
            Direction.NORTH -> headEntity.position.y -= speed
            Direction.EAST -> headEntity.position.x += speed
            Direction.SOUTH -> headEntity.position.y += speed
            Direction.WEST -> headEntity.position.x -= speed
            else -> {
            }
        }

        if (!Config.playerWarpsThroughWalls) {
            return
        }

        if (headEntity.position.x + headEntity.size.width / 2 < 0) {
            headEntity.position.x = Game.board.size.width - headEntity.size.width
        } else if (headEntity.position.x + headEntity.size.width / 2 > Game.board.size.width) {
            headEntity.position.x = 0
        }

        if (headEntity.position.y + headEntity.size.height / 2 < 0) {
            headEntity.position.y = Game.board.size.height - headEntity.size.height
        } else if (headEntity.position.y + headEntity.size.height / 2 > Game.board.size.height) {
            headEntity.position.y = 0
        }
    }

    private fun checkForCollision(spritesAtPosition: List<Sprite>): Boolean {
        if (direction == Direction.NONE) {
            return false
        }

        if (Config.snakeCollidesWithWalls && headIsOutOfBoard()) {
            Game.end("$name burst its head against the wall")
            return true
        }

        if (!spritesAtPosition.any { it.solid }) {
            return false
        }

        // Handle sprite collisions

        val snakeParts = spritesAtPosition.filterIsInstance<SnakePart>()
        if (snakeParts.isEmpty()) {
            Game.end("$name burst its head")
            return true
        }

        if (hasStarEffect()) {
            return processSnakeCollisionsDuringStartEffect(snakeParts)
        }

        return processSnakeCollisions(snakeParts)
    }

    private fun processSnakeCollisions(snakeParts: List<SnakePart>): Boolean {
        if (snakeParts.any { it is SnakeBody && it.snakePlayer == this }) {
            Game.end("$name ate itself")
            return true
        }

        val otherSnakeHead = snakeParts.find { it is SnakeHead }
        if (otherSnakeHead != null) {
            val otherSnake = (otherSnakeHead as SnakeHead).snakePlayer
            if (otherSnake.hasStarEffect()) {
                Game.end("$name was eaten alive by ${otherSnake.name}")
            } else {
                Game.end("Head to head collision between $name and ${otherSnake.name}")
            }
            return true
        }

        val otherSnakeBody = snakeParts.find { it is SnakeBody }
        if (otherSnakeBody != null) {
            Game.end("$name ran into ${(otherSnakeBody as SnakeBody).snakePlayer.name}")
            return true
        }

        Game.end("$name collided with someone")
        return true
    }

    private fun processSnakeCollisionsDuringStartEffect(snakeParts: List<SnakePart>): Boolean {
        val otherSnakeHead = snakeParts.find { it is SnakeHead }
        if (otherSnakeHead != null) {
            val otherSnake = (otherSnakeHead as SnakeHead).snakePlayer
            if (otherSnake.hasStarEffect()) {
                Game.end("Big Bang between $name and ${otherSnake.name}!")
            } else {
                Game.end("${otherSnake.name} was eaten alive by $name")
            }
            return true
        }

        val otherSnakeBody = snakeParts.find { it is SnakeBody }
        if (otherSnakeBody != null) {
            val otherSnake = (otherSnakeBody as SnakeBody).snakePlayer
            otherSnake.cutOffBodyAt(otherSnakeBody)
            return true
        }
        return false
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
    }

    private fun catchStar(star: Star) {
        logger.info("Player caught star")
        star.destroy()
        starWearsOutTime = Game.state.time + Config.starEffectTime * Config.stepPerSeconds
        logger.info("Player has been given the star effect until time: $starWearsOutTime")
    }

    private fun hasStarEffect() = starWearsOutTime > Game.state.time

    fun setControls(up: Int, right: Int, down: Int, left: Int) {
        upKey = up
        rightKey = right
        downKey = down
        leftKey = left
    }

    fun setRandomStartPosition() {
        startPosition = Game.board.getRandomEmptyPoint(headEntity.size) ?: return
    }

    override fun toString(): String {
        return "SnakePlayer(name=$name, color=$color, score=$score)"
    }
}