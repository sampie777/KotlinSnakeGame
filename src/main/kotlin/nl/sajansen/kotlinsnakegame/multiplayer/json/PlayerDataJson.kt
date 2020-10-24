package nl.sajansen.kotlinsnakegame.multiplayer.json

import nl.sajansen.kotlinsnakegame.objects.Direction
import java.awt.Color
import java.awt.Point


data class PlayerDataJson(
    var className: String,
    var name: String,
    var position: Point,
    var direction: Direction,
    var color: Color? = null
)