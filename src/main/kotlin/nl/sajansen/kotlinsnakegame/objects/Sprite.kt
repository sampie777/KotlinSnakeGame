package nl.sajansen.kotlinsnakegame.objects


import nl.sajansen.kotlinsnakegame.gui.utils.scaleImage
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage
import java.net.URL
import java.util.logging.Logger
import javax.imageio.ImageIO

abstract class Sprite : Entity {
    private val logger = Logger.getLogger(Sprite::class.java.name)

    open var position = Point(0, 0)
    open var size = Dimension(32, 32)
    open var sprite = Sprites.UNKNOWN
    open var solid = true

    private fun spriteResource(): URL? = this::class.java.classLoader.getResource(sprite.path)

    open fun paint(): BufferedImage {
        val spriteResource =
            spriteResource() ?: throw IllegalArgumentException("Sprite resource not found: ${sprite.path}")
        val image = ImageIO.read(spriteResource)
        return scaleImage(image, size.width, size.height)
    }
}