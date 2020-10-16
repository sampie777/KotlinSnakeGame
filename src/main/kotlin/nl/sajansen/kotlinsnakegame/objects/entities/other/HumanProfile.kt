package nl.sajansen.kotlinsnakegame.objects.entities.other


import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import java.util.logging.Logger

open class HumanProfile : Sprite() {
    private val logger = Logger.getLogger(HumanProfile::class.java.name)

    override var sprite = Sprites.PLAYER_FACE_1
    override var solid = false

    override fun step() {
    }
}