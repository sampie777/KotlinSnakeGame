package nl.sajansen.kotlinsnakegame.objects.board


import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.Entity
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.props.Box
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.isPointInSprite
import nl.sajansen.kotlinsnakegame.objects.isSpriteInSprite
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.random.Random

class Board : Entity {
    private val logger = Logger.getLogger(Board::class.java.name)

    var size = Dimension(900, 600)
    var windowSize = Dimension(900, 600)
    var windowPosition = Point(0, 0)
    var entities = arrayListOf<Sprite>()
    var gridSize = 32

    init {
        loadBoard1()
    }

    private fun loadBoard1() {
        entities.add(Box(Point(100, 60)))
        entities.add(Box(Point(311, 98)))
        spawnRandomFood()
    }

    private fun allSprites() = Game.players + entities

    override fun reset() {
        entities.clear()
        loadBoard1()

        Game.players.forEach {
            it.reset()
        }
    }

    override fun step() {
        allSprites().forEach {
            it.step()
        }
    }

    override fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(size.width, size.height)

        paintSprites(g, allSprites())

        g.dispose()
        return bufferedImage.getSubimage(windowPosition.x, windowPosition.y, windowSize.width, windowSize.height)
    }

    private fun paintSprites(g: Graphics2D, sprites: List<Sprite>) {
        sprites.forEach {
            if (it is SnakePlayer) {
               g.drawImage(it.paint(), null, 0, 0)
            } else {
                g.drawImage(it.paint(), null, it.position.x, it.position.y)
            }
        }
    }

    fun getSpritesAt(sprite: Sprite): List<Sprite> {
        return allSprites().filter { it != sprite }
            .filter { isSpriteInSprite(sprite, it) }
    }

    fun getSpritesAt(position: Point): List<Sprite> {
        return allSprites().filter { isPointInSprite(position, it) }
    }

    fun spawnRandomFood() {
        val food = Food(Point(0, 0))

        val maxX = size.width / gridSize
        val maxY = size.height / gridSize

        val maxTries = 100
        var currentTry = 0
        do {
            if (currentTry++ > maxTries) {
                logger.warning("Could not spawn new food: no empty random location found")
                Game.end("No room left for food")
                break
            }
            food.position.x = Random.nextInt(0, maxX) * gridSize + (gridSize - food.size.width) / 2
            food.position.y = Random.nextInt(0, maxY) * gridSize + (gridSize - food.size.height) / 2
        } while (getSpritesAt(food).isNotEmpty())

        entities.add(food)
    }
}