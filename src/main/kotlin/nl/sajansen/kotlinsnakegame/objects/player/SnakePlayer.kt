package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.ConfigEventListener
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeBody
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeHead
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import java.awt.Color
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.logging.Logger
import kotlin.math.max

class SnakePlayer(
    override var name: String = "Player",
    var color: Color = Color(0, 0, 0, 0),
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

    private var direction: Direction = Direction.NONE
    private var speed = Game.board.gridSize

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

        if (!isItTimeToUpdate()) {
            return
        }

        updateBodyPositions()
        moveToNewPosition()

        val spritesAtPosition = Game.board.getSpritesAt(headEntity)
        if (checkForCollision(spritesAtPosition)) {
            return
        }

        spritesAtPosition.filterIsInstance<Food>()
            .forEach {
                consume(it)
                addBodyEntityAt(bodyEntities[0].position, 0)

                Game.board.spawnRandomFood()
            }
    }

    private fun isItTimeToUpdate(): Boolean {
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

        val snakeParts = spritesAtPosition.filter { it is SnakeHead || it is SnakeBody }
        if (snakeParts.isEmpty()) {
            Game.end("$name burst its head")
            return true
        }

        if (snakeParts.any { it is SnakeBody && it.snakePlayer == this }) {
            Game.end("Snake ate itself")
            return true
        }

        val otherSnakeHead = snakeParts.find { it is SnakeHead }
        if (otherSnakeHead != null) {
            Game.end("Head to head collision between $name and ${(otherSnakeHead as SnakeHead).snakePlayer.name}")
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