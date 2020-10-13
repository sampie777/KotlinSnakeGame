package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeBody
import nl.sajansen.kotlinsnakegame.objects.entities.snake.SnakeHead
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

class SnakePlayer : Player(), KeyEventListener {
    private val logger = Logger.getLogger(SnakePlayer::class.java.name)

    override var speed = size.width

    // Just some empty values, see reset() for the real values
    override var score = 0
    private var updateInterval = 0
    private var nextUpdateTime: Long = 0
    private var headEntity = SnakeHead()
    private var bodyEntities = arrayListOf<SnakeBody>()

    override fun reset() {
        position = Point(0, 0)
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
        headEntity = SnakeHead()
        headEntity.position = position
        Game.board.entities.add(headEntity)

        // Creating new body
        bodyEntities.clear()
        while (bodyEntities.size < score) {
            addBodyEntityAt(headEntity.position)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        super<Player>.keyPressed(e)

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

    override fun step() {
        if (Game.state.runningState != GameRunningState.STARTED) {
            return
        }

        if (!isItTimeToUpdate()) {
            return
        }

        updateBodyPositions()
        moveToNewPosition()
        updateHeadPosition()

        val spritesAtPosition = Game.board.getSpritesAt(headEntity).filter { it != this }
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

    private fun checkForCollision(spritesAtPosition: List<Sprite>): Boolean {
        if (direction == Direction.NONE) {
            return false
        }

        if (spritesAtPosition.any { it.solid }) {
            Game.end("Snake burst its head at $position")
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
        return headEntity.position.x < 0 || headEntity.position.x + size.width > Game.board.size.width
                || headEntity.position.y < 0 || headEntity.position.y + size.height > Game.board.size.height
    }

    private fun updateHeadPosition() {
        headEntity.position = position
    }

    private fun addBodyEntityAt(point: Point, index: Int? = null) {
        val entity = SnakeBody()
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

    override fun paint(): BufferedImage {
        return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }
}