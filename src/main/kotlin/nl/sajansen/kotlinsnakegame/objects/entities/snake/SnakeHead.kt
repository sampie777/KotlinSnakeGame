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

    // Lidar
    private val radarTranslationToInner = 16
    override val maxViewDistance = radarTranslationToInner + 32
    override val minViewDistance = radarTranslationToInner + 2
    override val viewAngle = 180.0
    override var scanResult: LidarScanResult = LidarScanResult()

    override var sprite = Sprites.SNAKE_HEAD_1

    override fun step() {
    }

    override fun paint(): BufferedImage {
        return colorizeImage(super.paint(), snakePlayer.color)
    }

    override fun collidedWith(entity: Entity) {
        snakePlayer.headCollidedWith(entity)
    }

    /*****************
    LIDAR
     */

    override fun radarPosition(): Point {
        val center = Point(
            position.x + size.width / 2,
            position.y + size.height / 2
        )

        val orientation = Math.toRadians(radarOrientation())

        return Point(
            (center.x + (size.width / 2 - radarTranslationToInner) * sin(orientation)).roundToInt(),
            (center.y + (size.height / 2 - radarTranslationToInner) * -cos(orientation)).roundToInt(),
        )
    }

    override fun radarOrientation(): Double {
        return snakePlayer.direction.value / 8.0 * 360
    }
}