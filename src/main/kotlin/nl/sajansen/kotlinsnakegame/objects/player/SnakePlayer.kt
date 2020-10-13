package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
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

class SnakePlayer : Player, KeyEventListener {
    private val logger = Logger.getLogger(SnakePlayer::class.java.name)

    // Just some empty values, see reset() for the real values
    private var updateInterval = 0
    private var nextUpdateTime: Long = 0
    private var headEntity = SnakeHead(this)
    private var bodyEntities = arrayListOf<SnakeBody>()
    override var score = 0
        set(value) {
            field = value
            logger.info("Player scores increases to: $value")
        }

    override var name: String = "Player"
    var color: Color = Color(0, 0, 0, 0)
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
        direction = Direction.NONE
        score = 3
        nextUpdateTime = 0
        boostSpeed(false)
        initEntities()
    }

    private fun initEntities() {
        logger.info("Resetting snake's entities")
        headEntity.destroy()
        bodyEntities.forEach { it.destroy() }

        // Creating new head
        headEntity = SnakeHead(this)
        headEntity.position = Point(0, 0)
        Game.board.entities.add(headEntity)

        // Creating new body
        bodyEntities.clear()
        while (bodyEntities.size < score) {
            addBodyEntityAt(headEntity.position)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            upKey -> direction = Direction.NORTH
            rightKey -> direction = Direction.EAST
            downKey -> direction = Direction.SOUTH
            leftKey -> direction = Direction.WEST
        }

        if (e.keyCode == Config.snakeBoostKey) {
            boostSpeed(true)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == Config.snakeBoostKey) {
            boostSpeed(false)
        }
    }

    private fun boostSpeed(enable: Boolean) {
        updateInterval = if (enable) {
            5
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

        if (spritesAtPosition.any { it.solid }) {
            Game.end("Snake burst its head at ${headEntity.position}")
            return true
        }

        if (bodyEntities.any { it.position.x == headEntity.position.x && it.position.y == headEntity.position.y }) {
            Game.end("Snake burst its head against itself at ${headEntity.position}")
            return true
        }

        if (Config.snakeCollidesWithWalls && headIsOutOfBoard()) {
            Game.end("Snake burst its head against the wall at ${headEntity.position}")
            return true
        }
        return false
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
}