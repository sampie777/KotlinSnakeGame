package nl.sajansen.kotlinsnakegame.objects.board

import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage

interface Board {
    var gridSize: Int
    var size: Dimension
    var windowSize: Dimension
    var windowPosition: Point
    var entities: ArrayList<Entity>

    fun reset()
    fun step()
    fun paint(): BufferedImage
    fun getRandomEmptyPoint(size: Dimension): Point?

    fun spawnRandomFood() {}
    fun spawnRandomStar() {}
}