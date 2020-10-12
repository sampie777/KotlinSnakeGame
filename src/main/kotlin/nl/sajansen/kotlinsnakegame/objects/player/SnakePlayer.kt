package nl.sajansen.kotlinsnakegame.objects.player


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.gui.utils.scaleImage
import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.props.Food
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger
import javax.imageio.ImageIO

class SnakePlayer : Player(), KeyEventListener {
    private val logger = Logger.getLogger(SnakePlayer::class.java.name)

    override var sprite = Sprites.SNAKE_HEAD_1
    var spriteBody = Sprites.SNAKE_BODY_1
    override var speed = size.width

    // Just some empty values, see reset() for the real values
    override var score = 0
    private var updateInterval = 0
    private var nextUpdateTime: Long = 0
    private var lastPositions = arrayListOf<Point>()

    private fun spriteBodyResource() = this::class.java.classLoader.getResource(spriteBody.path)

    override fun reset() {
        position = Point(0, 0)
        direction = Direction.NONE
        score = 3
        nextUpdateTime = 0
        boostSpeed(false)
        initLastPositions()
    }

    private fun initLastPositions() {
        logger.info("Resetting snake's last positions")
        lastPositions.clear()
        while (lastPositions.size < score) {
            lastPositions.add(position.clone() as Point)
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
        if (!isItTimeToUpdate()) {
            return
        }

        updateBodyPositions()
        moveToNewPosition()

        val spritesAtPosition = Game.board.getSpritesAt(this)
        if (checkForCollision(spritesAtPosition)) {
            return
        }

        spritesAtPosition.filterIsInstance<Food>()
            .forEach {
                consume(it)
                lastPositions.add(0, lastPositions[0])

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
            Game.end("Snake burst its head")
            return true
        }

        if (lastPositions.any { it.x == position.x && it.y == position.y }) {
            Game.end("Snake burst its head against itself")
            return true
        }

        if (Config.snakeCollidesWithWalls && headIsOutOfBoard()) {
            Game.end("Snake burst its head against the wall")
            return true
        }
        return false
    }

    private fun headIsOutOfBoard(): Boolean {
        return position.x < 0 || position.x + size.width > Game.board.size.width
                || position.y < 0 || position.y + size.height > Game.board.size.height
    }

    private fun updateBodyPositions() {
        lastPositions.removeAt(0)
        lastPositions.add(position.clone() as Point)
    }

    override fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.size.width, Game.board.size.height)

        val head = paintSnakeHead()
        val bodyPart = paintSnakeBodyPart()

        val scaledBodyPart = scaleImage(bodyPart, size.width, size.height)
        lastPositions.toTypedArray().forEach {
            g.drawImage(scaledBodyPart, null, it.x, it.y)
        }

        g.drawImage(scaleImage(head, size.width, size.height), null, position.x, position.y)

        g.dispose()
        return bufferedImage
    }

    private fun paintSnakeHead(): BufferedImage {
        val spriteResource =
            spriteResource() ?: throw IllegalArgumentException("Sprite resource not found: ${sprite.path}")
        return ImageIO.read(spriteResource)
    }

    private fun paintSnakeBodyPart(): BufferedImage {
        val spriteBodyResource =
            spriteBodyResource() ?: throw IllegalArgumentException("Sprite resource not found: ${spriteBody.path}")
        return ImageIO.read(spriteBodyResource)
    }
}