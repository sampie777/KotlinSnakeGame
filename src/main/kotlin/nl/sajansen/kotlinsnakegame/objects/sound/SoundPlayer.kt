package nl.sajansen.kotlinsnakegame.objects.sound

import nl.sajansen.kotlinsnakegame.ApplicationInfo
import nl.sajansen.kotlinsnakegame.config.Config
import java.io.File
import java.util.logging.Logger
import javax.sound.sampled.*
import kotlin.math.min

object SoundPlayer {
    private val logger = Logger.getLogger(SoundPlayer::class.java.name)

    fun play(sound: Sounds, gain: Float = sound.defaultVolume) {
        val clip = getClip(sound) ?: return
        clip.gain = gain * Config.mainVolume
        clip.start()
    }

    private fun getClip(sound: Sounds): Clip? {
        val file = File(ApplicationInfo::class.java.classLoader.getResource(sound.path)!!.file)
        logger.info("Playing sound: ${file.absolutePath}")

        if (!file.exists()) {
            logger.warning("Sound file not found: ${file.absolutePath}")
            return null
        }

        try {
            val inputStream: AudioInputStream = AudioSystem.getAudioInputStream(file)
            val dataLineInfo = DataLine.Info(Clip::class.java, inputStream.format)

            val clip = AudioSystem.getLine(dataLineInfo) as Clip
            clip.open(inputStream)
            return clip
        } catch (e: Exception) {
            logger.severe("Failed to play sound: ${file.absolutePath}")
            e.printStackTrace()
        }
        return null
    }

    private fun Clip.volumeControl() = this.getControl(FloatControl.Type.VOLUME) as FloatControl

    private var Clip.gain: Float
        get() = volumeControl().value / volumeControl().maximum
        set(value) {
            volumeControl().value = min(volumeControl().maximum, value * volumeControl().maximum)
        }
}