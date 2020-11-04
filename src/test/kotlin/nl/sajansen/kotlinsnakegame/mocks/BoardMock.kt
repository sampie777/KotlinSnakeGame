package nl.sajansen.kotlinsnakegame.mocks


import nl.sajansen.kotlinsnakegame.objects.board.Board
import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.logging.Logger

class BoardMock : Board {
    private val logger = Logger.getLogger(BoardMock::class.java.name)
    override var gridSize: Int = 10
    override var size: Dimension = Dimension(100, 100)
    override var windowSize: Dimension = Dimension(100, 100)
    override var windowPosition: Point = Point(0, 0)
    override var entities: ArrayList<Entity> = arrayListOf()
    override var entitiesImage: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

    override fun reset() {
    }

    override fun step() {
    }

    override fun paint(): BufferedImage {
        TODO("Not yet implemented")
    }

    override fun getRandomEmptyPoint(size: Dimension): Point? {
        TODO("Not yet implemented")
    }
}