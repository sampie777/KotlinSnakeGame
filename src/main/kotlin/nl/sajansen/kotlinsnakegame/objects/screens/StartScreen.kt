package nl.sajansen.kotlinsnakegame.objects.screens


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Font
import java.awt.event.KeyEvent
import java.util.logging.Logger

object StartScreen : Screen(), KeyEventListener {
    private val logger = Logger.getLogger(StartScreen::class.java.name)

    override var backgroundOpacity = 255

    init {
        val titleLabel = Label(Config.pressKeyToStartMessage)
        titleLabel.font = Font("Dialog", Font.PLAIN, 30)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        titleLabel.componentAlignmentY = ComponentAlignment.CENTER
        add(titleLabel)

        GameOverlay.addControlsOverlay(this)
    }

    override fun keyReleased(e: KeyEvent) {
        if (Game.state.runningState != GameRunningState.RESET) return

        // Restart game on any key press
        close()
        Game.restart()
    }
}