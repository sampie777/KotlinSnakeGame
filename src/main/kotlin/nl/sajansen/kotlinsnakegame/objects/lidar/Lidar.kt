package nl.sajansen.kotlinsnakegame.objects.lidar

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import java.awt.*
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

object Lidar {
    private val logger = Logger.getLogger(Lidar::class.java.name)

    var objectsLayer: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    var beamsLayer: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

    const val radarResolution = 54  // Beams per 360 degrees
    private const val beamsPerDegree = radarResolution / 360.0

    fun scan(entities: List<LidarEquipped>) {
        scan(entities, objectsLayer)
    }

    fun scan(entities: List<LidarEquipped>, image: BufferedImage) {
        objectsLayer = image
        resetBeamsLayer()

        entities.forEach {
            val scanResult = scanInImage(it, objectsLayer)
            it.see(scanResult)

            if (Config.displayLidarBeams) {
                paintScanResult(scanResult)
            }
        }
    }

    private fun resetBeamsLayer() {
        beamsLayer = createGraphics(objectsLayer.width, objectsLayer.height).first
    }

    private fun scanInImage(entity: LidarEquipped, image: BufferedImage): LidarScanResult {
        val scanResult = LidarScanResult(entity.radarPosition())

        generateScanAngles(scanResult, entity)

        createBeamPaths(scanResult, entity)

        detectObjects(scanResult, entity, image)

        return scanResult
    }

    /**
     * Determine the angles of the lidar beams
     */
    private fun generateScanAngles(
        scanResult: LidarScanResult,
        entity: LidarEquipped
    ) {
        val startDegree = entity.radarOrientation() - entity.viewAngle / 2
        val beamsAmount = (entity.viewAngle * beamsPerDegree).toInt()

        (0 until beamsAmount)
            .map {
                val degreeIncrement = it * entity.viewAngle / (beamsAmount - 1)
                startDegree + degreeIncrement
            }
            .forEach { degree ->
                scanResult.detections.add(LidarDetection(degree))
            }
    }

    /**
     * Create a path of (unique) points along each lidar beam
     */
    private fun createBeamPaths(
        scanResult: LidarScanResult,
        entity: LidarEquipped
    ) {
        scanResult.detections
            .forEach {
                val radian = Math.toRadians(it.angle)
                val dX = sin(radian)
                val dY = -cos(radian)

                var x = entity.radarPosition().x.toDouble()
                var y = entity.radarPosition().y.toDouble()
                val beamPath = arrayListOf<Point>()

                while (entity.radarPosition().distance(x, y) <= entity.maxViewDistance) {
                    if (entity.radarPosition().distance(floor(x), floor(y)) >= entity.minViewDistance) {
                        beamPath.add(Point(x.toInt(), y.toInt()))
                    }

                    x += dX
                    y += dY
                }

                it.path = beamPath.distinct()
            }
    }

    /**
     * Check for non-transparent pixels for each point in the lidar beams/paths
     */
    private fun detectObjects(scanResult: LidarScanResult, entity: LidarEquipped, image: BufferedImage) {
        val imageRectangle = Rectangle(image.width, image.height)

        scanResult.detections
            .forEach {
                val furthestPoint = it.path.find { point ->
                    if (!imageRectangle.contains(point)) {
                        return@find false
                    }

                    val pixel = image.getRGB(point.x, point.y)
                    val color = Color(pixel, true)  // Create color of pixel value with alpha enabled

                    color.alpha != 0
                }

                if (furthestPoint == null) {
                    it.distance = entity.maxViewDistance.toDouble()
                    return@forEach
                }

                val pixel = image.getRGB(furthestPoint.x, furthestPoint.y)
                val color = Color(pixel, true)  // Create color of pixel value with alpha enabled
                it.intensity = color.alpha / 255.0
                it.distance = entity.radarPosition().distance(furthestPoint)
            }
    }

    private fun paintScanResult(scanResult: LidarScanResult) {
        val g = beamsLayer.createGraphics() as Graphics2D

        g.stroke = BasicStroke(1F)

        scanResult.detections.forEach {
            val nearestPoint = it.path.first()
            var furthestPoint = it.path.last()

            if (it.intensity == 0.0) {
                g.color = Color(110, 110, 110, 200)
            } else {
                g.color = Color(255, 0, 0, 200)

                val radianAngle = Math.toRadians(it.angle)
                furthestPoint = Point(
                    (scanResult.radarPosition.x + it.distance * sin(radianAngle)).roundToInt(),
                    (scanResult.radarPosition.y + it.distance * -cos(radianAngle)).roundToInt(),
                )
            }

            g.drawLine(nearestPoint.x, nearestPoint.y, furthestPoint.x, furthestPoint.y)
        }

        g.dispose()
    }
}