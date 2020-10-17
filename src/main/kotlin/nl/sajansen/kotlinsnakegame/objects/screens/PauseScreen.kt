package nl.sajansen.kotlinsnakegame.objects.screens


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Font
import java.util.logging.Logger

object PauseScreen : Screen() {
    private val logger = Logger.getLogger(PauseScreen::class.java.name)

    override var backgroundOpacity = 200
    override var paintAsOverlay = true

    init {
        val pauseLabel = Label(Config.gamePausedMessage)
        pauseLabel.font = Font("Dialog", Font.PLAIN, 30)
        pauseLabel.componentAlignmentX = ComponentAlignment.CENTER
        pauseLabel.componentAlignmentY = ComponentAlignment.CENTER
        add(pauseLabel)

        GameOverlay.addControlsOverlay(this)
    }
}