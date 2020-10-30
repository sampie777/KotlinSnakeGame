package nl.sajansen.kotlinsnakegame.objects.entities.other


import nl.sajansen.kotlinsnakegame.objects.Direction
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.sound.SoundPlayer
import nl.sajansen.kotlinsnakegame.objects.sound.Sounds
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.logging.Logger

open class Gnome : Sprite() {
    private val logger = Logger.getLogger(Gnome::class.java.name)

    override var sprite = Sprites.GNOME_NEUTRAL_1
    override var size = Dimension(19, 32)
    override var spriteSpeed = 3
    override var solid = false
    var direction: Direction = Direction.NONE

    private var nextSoundTime = 0L
    private val soundUpdateInterval = 4
    private var soundTrack = 0

    override fun reset() {
        super.reset()
        soundTrack = 0
        nextSoundTime = 0L
    }

    override fun step() {
        if (direction == Direction.NONE) return

        if (isItTimeToUpdateSound()) {
            playSound()
        }
    }

    override fun paint(): BufferedImage {
        sprite = when (direction) {
            Direction.NORTH -> Sprites.GNOME_WALKING_NORTH_1
            Direction.EAST -> Sprites.GNOME_WALKING_EAST_1
            Direction.SOUTH -> Sprites.GNOME_WALKING_SOUTH_1
            Direction.WEST -> Sprites.GNOME_WALKING_WEST_1
            else -> Sprites.GNOME_NEUTRAL_1
        }
        return super.paint()
    }

    private fun isItTimeToUpdateSound(): Boolean {
        if (nextSoundTime > Game.state.time) {
            return false
        }
        nextSoundTime = Game.state.time + soundUpdateInterval
        return true
    }

    private fun playSound() {
        soundTrack = ++soundTrack % 2

        when (soundTrack) {
            0 -> SoundPlayer.play(Sounds.STEP_1)
            else -> SoundPlayer.play(Sounds.STEP_2)
        }
    }
}