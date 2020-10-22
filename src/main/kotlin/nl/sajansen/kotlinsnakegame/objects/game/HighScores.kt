package nl.sajansen.kotlinsnakegame.objects.game

import nl.sajansen.kotlinsnakegame.ApplicationInfo
import nl.sajansen.kotlinsnakegame.objects.player.Player
import java.util.logging.Logger
import java.util.prefs.Preferences

object HighScores {
    private val logger = Logger.getLogger(HighScores::class.java.name)

    private val highScoresStorageNodeName = "${ApplicationInfo.name} highscores"
    private val highScoresStorage = Preferences.userRoot().node(highScoresStorageNodeName)

    fun setHighScore(player: Player, score: Int) {
        setHighScore(player.name, score)
    }

    fun setHighScore(name: String, score: Int) {
        highScoresStorage.putInt(name, score)
    }

    fun getHighScores(): List<Int> {
        return highScoresStorage.keys().map {
            highScoresStorage.getInt(it, 0)
        }
    }

    fun getHighScore(player: Player): Int = getHighScore(player.name)

    fun getHighScore(name: String): Int = highScoresStorage.getInt(name, 0)

    fun checkAndSetHighScore(player: Player) = checkAndSetHighScore(player, player.score)

    fun checkAndSetHighScore(player: Player, score: Int): Boolean {
        if (score <= getHighScore(player)) {
            return false
        }

        logger.info("${player.name} has got a new high score: $score")
        setHighScore(player, score)
        return true
    }

    fun clearHighScores() {
        logger.info("Clearing high scores")
        highScoresStorage.clear()
    }
}