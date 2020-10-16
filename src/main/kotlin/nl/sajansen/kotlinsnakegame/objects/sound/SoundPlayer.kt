package nl.sajansen.kotlinsnakegame.objects.sound

import nl.sajansen.kotlinsnakegame.ApplicationInfo
import sun.audio.AudioPlayer
import sun.audio.AudioStream
import java.io.File
import java.io.FileInputStream
import java.util.logging.Logger

object SoundPlayer {
    private val logger = Logger.getLogger(SoundPlayer::class.java.name)

    fun play(sound: Sounds) {
        val file = File(ApplicationInfo::class.java.classLoader.getResource(sound.path)!!.file)
        if (!file.exists()) {
            logger.warning("Sound file not found: ${file.absolutePath}")
        }

        val inputStream = FileInputStream(file)

        try {
            val audioStream = AudioStream(inputStream)
            AudioPlayer.player.start(audioStream)
        } catch (e: Exception) {
            logger.severe("Failed to play sound: ${file.absolutePath}")
            e.printStackTrace()
        }
    }
}