package nl.sajansen.kotlinsnakegame.objects.entities


import nl.sajansen.kotlinsnakegame.gui.utils.scaleImage
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.logging.Logger

abstract class Sprite : CollidableEntity {
    private val logger = Logger.getLogger(Sprite::class.java.name)

    override var position = Point(0, 0)
    override var size = Dimension(32, 32)
    open var sprite = Sprites.UNKNOWN
    open var solid = true

    open var spriteSpeed = 3
    private var nextUpdateTime: Long = 0
    private var spriteFrameIndex = 0

    override fun reset() {
        nextUpdateTime = 0
        spriteFrameIndex = 0
    }

    open fun paint(): BufferedImage {
        if (sprite.bufferedImage == null) {
            logger.warning("No image loaded for sprite. Returning empty image.")
            return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        }

        val frameImage = getCurrentSpriteFrame(sprite.bufferedImage!!)
        return scaleImage(frameImage, size.width, size.height)
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

    override fun collidedWith(entity: Entity) {
    }

    override fun destroy() {
        logger.fine("Destroying $this")
        super.destroy()
    }
}