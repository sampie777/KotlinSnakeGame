package nl.sajansen.kotlinsnakegame.objects

import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage

fun isPointInSprite(point: Point, sprite: Sprite): Boolean {
    return point.x > sprite.position.x && point.x < sprite.position.x + sprite.size.width &&
            point.y > sprite.position.y && point.y < sprite.position.y + sprite.size.height
}

fun isPointInArea(point: Point, areaPoint: Point, areaSize: Dimension): Boolean {
    return point.x > areaPoint.x && point.x < areaPoint.x + areaSize.width &&
            point.y > areaPoint.y && point.y < areaPoint.y + areaSize.height
}

fun isPointInSprite(point: Point, size: Dimension, sprite: Sprite): Boolean {
    return point.x + size.width > sprite.position.x
            && point.x < sprite.position.x + sprite.size.width
            && point.y + size.height > sprite.position.y
            && point.y < sprite.position.y + sprite.size.height
}

fun isSpriteInSprite(sprite1: Sprite, sprite2: Sprite): Boolean {
    return sprite1.position.x + sprite1.size.width > sprite2.position.x
            && sprite1.position.x < sprite2.position.x + sprite2.size.width
            && sprite1.position.y + sprite1.size.height > sprite2.position.y
            && sprite1.position.y < sprite2.position.y + sprite2.size.height
}

fun isEntityInEntity(entity1: Entity, entity2: Entity): Boolean {
    return entity1.position.x + entity1.size.width > entity2.position.x
            && entity1.position.x < entity2.position.x + entity2.size.width
            && entity1.position.y + entity1.size.height > entity2.position.y
            && entity1.position.y < entity2.position.y + entity2.size.height
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

/**
 * Warp object through walls to the other side.
 * sizeMarginFactor: specify the amount of the object which is allowed to be outside of the walls before triggering the warp
 */
@Suppress("DuplicatedCode")
fun adjustPositionForWall(position: Point, size: Dimension, sizeMarginFactor: Double = 0.5) {
    val widthMargin = (size.width * sizeMarginFactor).toInt()
    if (position.x + widthMargin < 0) {
        position.x = (Game.board.size.width - (size.width - widthMargin))
    } else if (position.x + (size.width - widthMargin) > Game.board.size.width) {
        position.x = -widthMargin
    }

    val heightMargin = (size.height * sizeMarginFactor).toInt()
    if (position.y + heightMargin < 0) {
        position.y = (Game.board.size.height - (size.height - heightMargin))
    } else if (position.y + (size.height - heightMargin) > Game.board.size.height) {
        position.y = -heightMargin
    }
}

fun getAvailableColorForSnake(player: SnakePlayer) {
    val availableColors = SnakePlayer.availableColors
        .filterNot { color ->
            Game.players.any { it != player && it is SnakePlayer && it.color == color }
        }
    player.color = availableColors.firstOrNull() ?: SnakePlayer.availableColors.first()
}