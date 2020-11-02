package nl.sajansen.kotlinsnakegame.objects.lidar


import java.awt.Point

data class LidarScanResult(
    val radarPosition: Point,
    val angles: ArrayList<Double> = arrayListOf(),
    val beamPaths: ArrayList<List<Point>> = arrayListOf(),
    val objectDetectionDistances: ArrayList<Double> = arrayListOf(),
)
