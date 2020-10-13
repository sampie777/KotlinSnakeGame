package nl.sajansen.kotlinsnakegame.objects

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage

fun isPointInSprite(point: Point, sprite: Sprite): Boolean {
    return point.x > sprite.position.x && point.x < sprite.position.x + sprite.size.width &&
            point.y > sprite.position.y && point.y < sprite.position.y + sprite.size.height
}

fun isSpriteInSprite(sprite1: Sprite, sprite2: Sprite): Boolean {
    return sprite1.position.x + sprite1.size.width > sprite2.position.x
            && sprite1.position.x < sprite2.position.x + sprite2.size.width
            && sprite1.position.y + sprite1.size.height > sprite2.position.y
            && sprite1.position.y < sprite2.position.y + sprite2.size.height
}

fun colorizeImage(image: BufferedImage, color: Color): BufferedImage {
    val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
    val g = bufferedImage.createGraphics()

    g.drawImage(image, 0, 0, null)
    g.composite = AlphaComposite.SrcAtop
    g.color = color
    g.fillRect(0, 0, bufferedImage.width, bufferedImage.height)

    g.dispose()
    return bufferedImage
}