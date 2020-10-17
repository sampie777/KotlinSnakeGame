package nl.sajansen.kotlinsnakegame.objects.sound

import nl.sajansen.kotlinsnakegame.ApplicationInfo
import java.io.File
import java.util.logging.Logger
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine


object SoundPlayer {
    private val logger = Logger.getLogger(SoundPlayer::class.java.name)

    fun play(sound: Sounds) {
        val file = File(ApplicationInfo::class.java.classLoader.getResource(sound.path)!!.file)
        logger.info("Playing sound: ${file.absolutePath}")

        if (!file.exists()) {
            logger.warning("Sound file not found: ${file.absolutePath}")
        }

        try {
            val inputStream: AudioInputStream = AudioSystem.getAudioInputStream(file)
            val dataLineInfo = DataLine.Info(Clip::class.java, inputStream.format)

            val clip = AudioSystem.getLine(dataLineInfo) as Clip
            clip.open(inputStream)
            clip.start()
        } catch (e: Exception) {
            logger.severe("Failed to play sound: ${file.absolutePath}")
            e.printStackTrace()
        }
    }
}