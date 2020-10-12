package nl.sajansen.kotlinsnakegame.config

import java.awt.event.KeyEvent
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

object Config {
    private val logger = Logger.getLogger(Config.toString())

    // GUI
    var paintFPS: Long = 25

    // Game
    var stepInterval: Long = 25
    var snakeStepInterval = 15

    // Controls
    var playerUpKey = KeyEvent.VK_UP
    var playerRightKey = KeyEvent.VK_RIGHT
    var playerDownKey = KeyEvent.VK_DOWN
    var playerLeftKey = KeyEvent.VK_LEFT
    var snakeBoostKey = KeyEvent.VK_SPACE

    // String
    var pressKeyToStartMessage = "Press key to start".toUpperCase()
    var gameOverMessage = "Game over".toUpperCase()
    var gamePausedMessage = "Paused".toUpperCase()

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