package nl.sajansen.kotlinsnakegame.config

import java.awt.event.KeyEvent
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

object Config {
    private val logger = Logger.getLogger(Config.toString())

    // Feature toggles
    var onScreenButtons = true

    // GUI
    var maxFps: Long = 25
    var fontFamily = "Dialog"
    var mainVolume: Float = 0.8f

    // Game
    var stepPerSeconds: Long = 25
    var playerWarpsThroughWalls = true
    var maxPlayers = 6

    var snakeCollidesWithWalls = !playerWarpsThroughWalls
    var snakeStepInterval = 13
    var snakeOnlyLeftRightControls = true
    var snakeEatsHumanPlayer = true

    var starMinSpawnTime = 1   // In seconds
    var starMaxSpawnTime = 10  // In seconds
    var starEffectTime = 7   // In seconds
    var starMinDieTimeout = 5
    var starMaxDieTimeout = 15

    // Controls
    var player1UpKey = KeyEvent.VK_UP
    var player1RightKey = KeyEvent.VK_RIGHT
    var player1DownKey = KeyEvent.VK_DOWN
    var player1LeftKey = KeyEvent.VK_LEFT
    var player2UpKey = KeyEvent.VK_W
    var player2RightKey = KeyEvent.VK_D
    var player2DownKey = KeyEvent.VK_S
    var player2LeftKey = KeyEvent.VK_A

    var playerPushFood = KeyEvent.VK_CONTROL
    var snakeBoostKey = KeyEvent.VK_SPACE

    // String
    var titleMessage = "Snake renewed".toUpperCase()
    var gameOverMessage = "Game over".toUpperCase()
    var gamePausedMessage = "Paused".toUpperCase()

    // Multiplayer
    var serverPort = 11100
    var maxDataPacketLength = 1024

    fun load() {
        try {
            PropertyLoader.load()
            PropertyLoader.loadConfig(this::class.java)
        } catch (e: Exception) {
            logger.severe("Failed to load Config")
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            if (PropertyLoader.saveConfig(this::class.java)) {
                PropertyLoader.save()
            }
        } catch (e: Exception) {
            logger.severe("Failed to save Config")
            e.printStackTrace()
        }
    }

    fun get(key: String): Any? {
        try {
            return javaClass.getDeclaredField(key).get(this)
        } catch (e: Exception) {
            logger.severe("Could not get config key $key")
            e.printStackTrace()
        }
        return null
    }

    fun set(key: String, value: Any?) {
        try {
            javaClass.getDeclaredField(key).set(this, value)
        } catch (e: Exception) {
            logger.severe("Could not set config key $key")
            e.printStackTrace()
        }
    }

    fun enableWriteToFile(value: Boolean) {
        PropertyLoader.writeToFile = value
    }

    fun fields(): List<Field> {
        val fields = javaClass.declaredFields.filter {
            it.name != "INSTANCE" && it.name != "logger"
                    && Modifier.isStatic(it.modifiers)
        }
        fields.forEach { it.isAccessible = true }
        return fields
    }
}