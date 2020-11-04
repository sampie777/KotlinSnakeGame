package nl.sajansen.kotlinsnakegame.objects.lidar


import java.awt.Point

data class LidarScanResult(
    val radarPosition: Point = Point(0, 0),
    var detections: ArrayList<LidarDetection> = arrayListOf(),
)

data class LidarDetection(
    val angle: Double,  // Angle at which this detection is made
    var distance: Double = -1.0,    // Distance from radarPosition to first reflection
    var path: List<Point> = emptyList(),    // Path of LIDAR beam
    var intensity: Double = 0.0,    // 0.0 if space is empty, 1.0 for strong reflection
)
