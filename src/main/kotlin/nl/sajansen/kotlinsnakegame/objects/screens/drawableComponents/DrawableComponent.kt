package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents

import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage

interface DrawableComponent {
    var position: Point
    var size: Dimension
    var componentAlignmentX: ComponentAlignment
    var componentAlignmentY: ComponentAlignment
    var isVisible: Boolean

    fun paint(): BufferedImage
}

enum class ComponentAlignment {
    LEFT,
    CENTER,
    RIGHT,
    TOP,
    BOTTOM
}
