package nl.sajansen.kotlinsnakegame.objects.screens


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.HighScores
import nl.sajansen.kotlinsnakegame.objects.player.Player
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

    init {
        initGui()
    }

    fun rebuildGui() {
        components.clear()
        initGui()
    }

    private fun initGui() {
        val titleLabel = Label(Config.gameOverMessage)
        titleLabel.font = Font("Dialog", Font.BOLD, 46)
        titleLabel.position = Point(0, 80)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        add(titleLabel)

        val messageLabel = Label(Game.deathMessage)
        messageLabel.font = Font("Dialog", Font.ITALIC, 22)
        messageLabel.fontColor = Color(230, 230, 230)
        messageLabel.position = Point(0, 140)
        messageLabel.componentAlignmentX = ComponentAlignment.CENTER
        add(messageLabel)

        showScores(Point(300, 250))

        GameOverlay.addControlsOverlay(this)
    }

    private fun showScores(position: Point) {
        val rowVerticalMargin = 45

        val label = Label("SCORES")
        label.font = Font("Dialog", Font.BOLD, 12)
        label.position = position
        label.componentAlignmentX = ComponentAlignment.CENTER
        label.fontColor = Color(173, 173, 173)
        components.add(label)

        Game.players
            .sortedBy { it.name }
            .sortedByDescending { it.score }
            .forEachIndexed { index, player ->
                paintScore(
                    Point(position.x, position.y + 30 + index * rowVerticalMargin),
                    player
                )
            }
    }

    private fun paintScore(position: Point, player: Player) {
        val nameLabel = Label(player.name)
        nameLabel.position = position
        nameLabel.font = Font("Dialog", Font.PLAIN, 24)
        nameLabel.fontColor = Color(217, 217, 217)
        components.add(nameLabel)

        val scoreLabel = Label(player.score.toString())
        scoreLabel.position = Point(position.x + 250, position.y)
        scoreLabel.fontColor = Color(217, 217, 217)
        components.add(scoreLabel)

        if (player.score > 0 && HighScores.getHighScore(player) == player.score) {
            scoreLabel.font = Font("Dialog", Font.BOLD, 24)
        } else {
            scoreLabel.font = Font("Dialog", Font.PLAIN, 24)
        }
    }

    override fun show(index: Int?) {
        super.show(index)
        rebuildGui()
    }
}