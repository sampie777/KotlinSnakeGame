package nl.sajansen.kotlinsnakegame.objects.lidar

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

    private const val beamsPerRadar = 9

    fun scan(entities: List<LidarEquipped>) {
        scan(entities, objectsLayer)
    }

    fun scan(entities: List<LidarEquipped>, image: BufferedImage) {
        objectsLayer = image
        resetBeamsLayer()

        entities.forEach {
            val scanResult = scanInImage(it, objectsLayer)
            it.see(scanResult)
            paintScanResult(scanResult)
        }
    }

    private fun resetBeamsLayer() {
        beamsLayer = createGraphics(objectsLayer.width, objectsLayer.height).first
    }

    private fun scanInImage(entity: LidarEquipped, image: BufferedImage): LidarScanResult {
        val scanResult = LidarScanResult(entity.radarPosition())

        generateScanAngles(scanResult, entity, beamsPerRadar)

        createBeamPaths(scanResult, entity)

        detectObjects(scanResult, entity, image)

        return scanResult
    }

    private fun generateScanAngles(
        scanResult: LidarScanResult,
        entity: LidarEquipped,
        beamsAmount: Int
    ) {
        val startDegree = entity.radarOrientation() - entity.viewAngle / 2

        (0 until beamsAmount)
            .map {
                val degreeIncrement = it * entity.viewAngle / (beamsAmount - 1)
                startDegree + degreeIncrement
            }
            .forEach { degree ->
                scanResult.angles.add(degree)
            }
    }

    private fun createBeamPaths(
        scanResult: LidarScanResult,
        entity: LidarEquipped
    ) {
        scanResult.angles
            .map(Math::toRadians)
            .map { radian ->
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

                beamPath.distinct()
            }
            .forEach { path ->
                scanResult.beamPaths.add(path)
            }
    }

    private fun detectObjects(scanResult: LidarScanResult, entity: LidarEquipped, image: BufferedImage) {
        val imageRectangle = Rectangle(image.width, image.height)

        scanResult.beamPaths
            .map { path ->
                val furthestPoint = path.find { point ->
                    if (!imageRectangle.contains(point)) {
                        return@find false
                    }

                    val pixel = image.getRGB(point.x, point.y)
                    val color = Color(pixel, true)  // Create color of pixel value with alpha enabled

                    color.alpha != 0
                }

                if (furthestPoint == null) {
                    return@map -1.0
                }

                entity.radarPosition().distance(furthestPoint)
            }
            .forEach {
                scanResult.objectDetectionDistances.add(it)
            }
    }

    private fun paintScanResult(scanResult: LidarScanResult) {
        val g = beamsLayer.createGraphics() as Graphics2D

        g.color = Color.GRAY
        g.stroke = BasicStroke(1F)

        scanResult.beamPaths.forEach {
            if (it.size < 2) {
                return@forEach
            }

            g.drawLine(it.first().x, it.first().y, it.last().x, it.last().y)
        }

        g.color = Color.RED
        scanResult.objectDetectionDistances.forEachIndexed { index, distance ->
            if (distance < 0) {
                return@forEachIndexed
            }

            val radianAngle = Math.toRadians(scanResult.angles[index])
            val endPoint = Point(
                (scanResult.radarPosition.x + distance * sin(radianAngle)).roundToInt(),
                (scanResult.radarPosition.y + distance * -cos(radianAngle)).roundToInt(),
            )
            g.drawLine(scanResult.radarPosition.x, scanResult.radarPosition.y, endPoint.x, endPoint.y)
        }

        g.dispose()
    }
}