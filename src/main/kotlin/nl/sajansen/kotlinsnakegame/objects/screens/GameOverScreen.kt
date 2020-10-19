package nl.sajansen.kotlinsnakegame.objects.screens


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Color
import java.awt.Font
import java.awt.Point
import java.util.logging.Logger

object GameOverScreen : Screen() {
    private val logger = Logger.getLogger(GameOverScreen::class.java.name)

    override var backgroundOpacity = 200
    override var paintAsOverlay = true

    private val messageLabel = Label()

    init {
        val titleLabel = Label(Config.gameOverMessage)
        titleLabel.font = Font("Dialog", Font.PLAIN, 30)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        titleLabel.componentAlignmentY = ComponentAlignment.CENTER
        add(titleLabel)

        messageLabel.font = Font("Dialog", Font.PLAIN, 22)
        messageLabel.fontColor = Color(230, 230, 230)
        messageLabel.componentAlignmentX = ComponentAlignment.CENTER
        messageLabel.position = Point(0, Game.board.windowSize.height / 2 + 25)
        add(messageLabel)

        GameOverlay.addControlsOverlay(this)
    }

    override fun show(index: Int?) {
        super.show(index)
        messageLabel.text = Game.deathMessage
    }
}