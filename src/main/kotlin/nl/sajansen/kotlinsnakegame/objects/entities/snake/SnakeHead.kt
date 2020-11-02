package nl.sajansen.kotlinsnakegame.objects.entities.snake


import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.colorizeImage
import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.lidar.LidarEquipped
import nl.sajansen.kotlinsnakegame.objects.lidar.LidarScanResult
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class SnakeHead(override val snakePlayer: SnakePlayer) : SnakePart, Sprite(), LidarEquipped {
    private val logger = Logger.getLogger(SnakeHead::class.java.name)

    override val maxViewDistance = 32
    override val minViewDistance = 4
    override val viewAngle = 90.0

    override var sprite = Sprites.SNAKE_HEAD_1

    override fun step() {
    }

    override fun paint(): BufferedImage {
        return colorizeImage(super.paint(), snakePlayer.color)
    }

    override fun collidedWith(entity: Entity) {
        snakePlayer.headCollidedWith(entity)
    }

    override fun see(scanResult: LidarScanResult) {
        snakePlayer.see(scanResult)
    }

    override fun radarPosition(): Point {
        val center = Point(
            position.x + size.width / 2,
            position.y + size.height / 2
        )

        val orientation = Math.toRadians(radarOrientation())

        return Point(
            (center.x + size.width / 2 * sin(orientation)).roundToInt(),
            (center.y + size.height / 2 * -cos(orientation)).roundToInt(),
        )
    }

    override fun radarOrientation(): Double {
        return snakePlayer.radarOrientation()
    }
}