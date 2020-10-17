package nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents

import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage

interface DrawableComponent {
    var position: Point
    var size: Dimension
    var componentAlignmentX: ComponentAlignment
    var componentAlignmentY: ComponentAlignment

    fun paint(): BufferedImage
}

enum class ComponentAlignment {
    LEFT,
    CENTER,
    RIGHT,
    TOP,
    BOTTOM
}
