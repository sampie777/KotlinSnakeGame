package nl.sajansen.kotlinsnakegame.objects.lidar

import java.awt.Point

interface LidarEquipped {
    val maxViewDistance: Int
    val minViewDistance: Int
    val viewAngle: Double
    var scanResult: LidarScanResult

    fun radarPosition(): Point
    fun radarOrientation(): Double
}