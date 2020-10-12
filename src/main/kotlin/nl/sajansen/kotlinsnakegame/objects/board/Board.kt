package nl.sajansen.kotlinsnakegame.objects.board


import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.GameObject
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.isPointInSprite
import nl.sajansen.kotlinsnakegame.objects.isSpriteInSprite
import nl.sajansen.kotlinsnakegame.objects.props.Box
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.logging.Logger

class Board : GameObject {
    private val logger = Logger.getLogger(Board::class.java.name)

    var size = Dimension(900, 600)
    var visibleSize = Dimension(900, 600)
    var props = arrayListOf<Sprite>()

    init {
        loadBoard1()
    }

    fun loadBoard1() {
        props.add(Box(Point(100, 60)))
        props.add(Box(Point(311, 98)))
    }

    private fun allSprites() = Game.players + props

    override fun reset() {
        allSprites().forEach {
            it.reset()
        }
    }

    override fun step() {
        allSprites().forEach {
            it.step()
        }
    }

    override fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(visibleSize.width, visibleSize.height)

        paintSprites(g, allSprites())

        g.dispose()
        return bufferedImage
    }

    private fun paintSprites(g: Graphics2D, sprites: List<Sprite>) {
        sprites.forEach {
            g.drawImage(it.paint(), null, it.position.x, it.position.y)
        }
    }

    fun isSolidObstacleAt(sprite: Sprite): Boolean {
        return allSprites().filter { it.solid && it != sprite}
            .find { isSpriteInSprite(sprite, it) } != null
    }

    fun isSolidObstacleAt(position: Point): Boolean {
        return allSprites().filter { it.solid }
            .find { isPointInSprite(position, it) } != null
    }
}