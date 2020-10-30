package nl.sajansen.kotlinsnakegame.objects.sound

import nl.sajansen.kotlinsnakegame.ApplicationInfo
import nl.sajansen.kotlinsnakegame.config.Config
import java.io.File
import java.util.logging.Logger
import javax.sound.sampled.*
import kotlin.math.min

object SoundPlayer {
    private val logger = Logger.getLogger(SoundPlayer::class.java.name)

    private val clips: Array<Clip?> = arrayOfNulls(Config.audioTracks)
    private var currentClipIndex = 0

    fun play(sound: Sounds, gain: Float = sound.defaultVolume) {
        val clip = getClip(sound) ?: return
        clip.gain = gain * Config.mainVolume
        clip.start()
    }

    private fun getClip(sound: Sounds): Clip? {
        val file = try {
            File(ApplicationInfo::class.java.classLoader.getResource(sound.path)!!.file)
        } catch (e: Exception) {
            logger.severe("Could not find sound file: ${sound.path}")
            e.printStackTrace()
            return null
        }

        if (!file.exists()) {
            logger.warning("Sound file does not exists: ${file.absolutePath}")
            return null
        }

        logger.info("Playing sound: ${file.absolutePath}")
        try {
            val inputStream: AudioInputStream = AudioSystem.getAudioInputStream(file)
            val dataLineInfo = DataLine.Info(Clip::class.java, inputStream.format)

            val clip = getNextAvailableClip(dataLineInfo)
            clip.open(inputStream)
            return clip
        } catch (e: Exception) {
            logger.severe("Failed to play sound: ${file.absolutePath}")
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get next clip in clips array. If null, create a new one, otherwise close the precious one.
     * By using an fixed amount of clips, it is hoped to reduce system load.
     */
    private fun getNextAvailableClip(dataLineInfo: DataLine.Info): Clip {
        currentClipIndex++
        if (currentClipIndex >= clips.size) {
            currentClipIndex = 0
        }

        clips[currentClipIndex]?.close()
        clips[currentClipIndex] = AudioSystem.getLine(dataLineInfo) as Clip

        return clips[currentClipIndex]!!
    }

    private fun Clip.volumeControl() = this.getControl(FloatControl.Type.VOLUME) as FloatControl

    private var Clip.gain: Float
        get() = volumeControl().value / volumeControl().maximum
        set(value) {
            volumeControl().value = min(volumeControl().maximum, value * volumeControl().maximum)
        }
}