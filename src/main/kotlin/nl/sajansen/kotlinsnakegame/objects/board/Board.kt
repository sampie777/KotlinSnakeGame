package nl.sajansen.kotlinsnakegame.objects.board


import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.GameObject
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.isPointInSprite
import nl.sajansen.kotlinsnakegame.objects.isSpriteInSprite
import nl.sajansen.kotlinsnakegame.objects.props.Box
import nl.sajansen.kotlinsnakegame.objects.props.Food
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

    private fun loadBoard1() {
        props.add(Box(Point(100, 60)))
        props.add(Box(Point(311, 98)))
        props.add(Food(Point(50, 50)))
        props.add(Food(Point(50, 100)))
        props.add(Food(Point(130, 100)))
    }

    private fun allSprites() = Game.players + props

    override fun reset() {
        props.clear()
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

    fun getSpritesAt(sprite: Sprite): List<Sprite> {
        return allSprites().filter { it != sprite }
            .filter { isSpriteInSprite(sprite, it) }
    }

    fun getSpritesAt(position: Point): List<Sprite> {
        return allSprites().filter { isPointInSprite(position, it) }
    }
}