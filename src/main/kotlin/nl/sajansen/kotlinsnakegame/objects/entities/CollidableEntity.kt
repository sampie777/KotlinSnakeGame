package nl.sajansen.kotlinsnakegame.objects.entities

interface CollidableEntity : Entity {
    fun collidedWith(entity: Entity)
}