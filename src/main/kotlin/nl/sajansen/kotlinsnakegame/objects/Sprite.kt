package nl.sajansen.kotlinsnakegame.objects


import nl.sajansen.kotlinsnakegame.gui.utils.scaleImage
import nl.sajansen.kotlinsnakegame.objects.game.Game
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

    open var spriteSpeed = 3
    private var nextUpdateTime: Long = 0
    private var spriteFrameIndex = 0
    private var bufferedImage: BufferedImage? = null

    private fun spriteResource(): URL? = this::class.java.classLoader.getResource(sprite.path)

    open fun paint(): BufferedImage {
        loadBufferedImage()

        if (bufferedImage == null) {
            logger.warning("No image loaded for sprite. Returning empty image.")
            return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        }

        val frameImage = getCurrentSpriteFrame(bufferedImage!!)
        return scaleImage(frameImage, size.width, size.height)
    }

    /**
     * Buffer sprite image
     */
    private fun loadBufferedImage() {
        if (bufferedImage != null) {
            return
        }

        val spriteResource =
            spriteResource() ?: throw IllegalArgumentException("Sprite resource not found: ${sprite.path}")
        bufferedImage = ImageIO.read(spriteResource)
    }

    private fun getCurrentSpriteFrame(image: BufferedImage): BufferedImage {
        if (sprite.frames == 1) {
            return image
        }

        val frameWidth = image.width / sprite.frames
        val frameStart = spriteFrameIndex * frameWidth

        if (isItTimeToUpdate()) {
            if (++spriteFrameIndex >= sprite.frames) {
                spriteFrameIndex = 0
            }
        }

        return image.getSubimage(frameStart, 0, frameWidth, image.height)
    }

    private fun isItTimeToUpdate(): Boolean {
        if (nextUpdateTime > Game.state.time) {
            return false
        }
        nextUpdateTime = Game.state.time + spriteSpeed
        return true
    }
}