package nl.sajansen.kotlinsnakegame.objects.screens


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Font
import java.awt.Graphics2D
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

    override fun paint(g: Graphics2D) {
        super.paint(g)

        if (Game.state.runningState != GameRunningState.PAUSED) {
            close()
        }
    }
}