package nl.sajansen.kotlinsnakegame.objects

import java.awt.Point

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