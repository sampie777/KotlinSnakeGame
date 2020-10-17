package nl.sajansen.kotlinsnakegame.objects.visuals

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.gui.utils.drawImageInXCenter
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents.Button
import nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents.DrawableComponent
import nl.sajansen.kotlinsnakegame.objects.visuals.settingsScreen.SettingsScreen
import java.awt.*
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.math.roundToInt

object GameOverlay : Screen() {
    private val logger = Logger.getLogger(GameOverlay::class.java.name)

    override var components = arrayListOf<DrawableComponent>()
    override var backgroundOpacity = 0

    init {
        if (Config.onScreenButtons) {
            val settingsButton = Button("S")
            settingsButton.position = Point(10, 10)
            settingsButton.backgroundColor = Color(255, 255, 255, 10)
            settingsButton.size = Dimension(30, 30)
            settingsButton.margin = Dimension(5, 5)
            settingsButton.onClick = {
                logger.info("Settings button clicked")
                Game.pause()
                SettingsScreen().show()
            }
            components.add(settingsButton)
        }
    }

    override fun paint(g: Graphics2D) {
        // painting stuff
        when (Game.state.runningState) {
            GameRunningState.RESET -> {
                paintPressKeyToStartScreen(g)
                paintControlsOverlay(g)
            }
            GameRunningState.ENDED -> {
                paintGameOverScreen(g)
                paintControlsOverlay(g)
            }
            else -> {
                if (Game.state.runningState == GameRunningState.PAUSED) {
                    paintGamePausedScreen(g)
                    paintControlsOverlay(g)
                }

                drawImageInXCenter(g, 0, Game.board.windowSize.width, paintScoreOverlay())
            }
        }
    }

    private fun paintControlsOverlay(g: Graphics2D) {
        drawImageInXCenter(g, 0, Game.board.windowSize.width, controlsOverlay())
    }

    private fun controlsOverlay(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.windowSize.width, Game.board.windowSize.height)

        super.paint(g)

        g.dispose()
        return bufferedImage
    }

    private fun paintScoreOverlay(): BufferedImage {
        if (Game.players.size == 1) {
            return paintScoreOverlayForOnePlayer()
        } else if (Game.players.size == 2) {
            return paintScoreOverlayForTwoPlayers()
        }

        return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }

    private fun paintScoreOverlayForOnePlayer(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(200, 35)

        // Draw outline
        val outline = Polygon()
        outline.addPoint(0, -3)
        outline.addPoint(20, bufferedImage.height)
        outline.addPoint(bufferedImage.width - 20, bufferedImage.height)
        outline.addPoint(bufferedImage.width, -3)

        g.color = Color.GRAY
        g.stroke = BasicStroke(3F)
        g.drawPolygon(outline)

        // Draw score text
        g.font = Font("Dialog", Font.BOLD, 20)
        g.color = Color.WHITE

        val message = Game.players[0].score.toString()
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.drawString(message, (bufferedImage.width - textWidth) / 2, textHeight)

        g.dispose()
        return bufferedImage
    }

    private fun paintScoreOverlayForTwoPlayers(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.windowSize.width, 35)
        val polygonWidth = 150

        // Draw outline
        val outline1 = Polygon()
        outline1.addPoint(-3, -3)
        outline1.addPoint(-3, bufferedImage.height)
        outline1.addPoint(polygonWidth - 20, bufferedImage.height)
        outline1.addPoint(polygonWidth, -3)

        val outline2 = Polygon()
        outline2.addPoint(bufferedImage.width + 3, -3)
        outline2.addPoint(bufferedImage.width + 3, bufferedImage.height)
        outline2.addPoint(bufferedImage.width - polygonWidth + 20, bufferedImage.height)
        outline2.addPoint(bufferedImage.width - polygonWidth, -3)

        g.color = Color.GRAY
        g.stroke = BasicStroke(3F)
        g.drawPolygon(outline1)
        g.drawPolygon(outline2)

        // Draw score text
        g.font = Font("Dialog", Font.BOLD, 20)
        g.color = Color.WHITE

        val message1 = Game.players[0].score.toString()
        val textWidth1 = g.fontMetrics.stringWidth(message1)
        g.drawString(message1, (polygonWidth - textWidth1) / 2, g.fontMetrics.height)

        val message2 = Game.players[1].score.toString()
        val textWidth2 = g.fontMetrics.stringWidth(message2)
        g.drawString(message2, bufferedImage.width - (polygonWidth - textWidth2) / 2, g.fontMetrics.height)

        g.dispose()
        return bufferedImage
    }

    private fun paintPressKeyToStartScreen(g: Graphics2D) {
        paintBackground(g)

        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.pressKeyToStartMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(
            message,
            (Game.board.windowSize.width - textWidth) / 2,
            ((Game.board.windowSize.height + textHeight * 0.7) / 2).roundToInt()
        )
    }

    private fun paintGamePausedScreen(g: Graphics2D) {
        paintBackground(g, 200)

        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.gamePausedMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(
            message,
            (Game.board.windowSize.width - textWidth) / 2,
            ((Game.board.windowSize.height + textHeight * 0.7) / 2).roundToInt()
        )
    }

    private fun paintGameOverScreen(g: Graphics2D) {
        paintBackground(g, 200)

        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.gameOverMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(
            message,
            (Game.board.windowSize.width - textWidth) / 2,
            ((Game.board.windowSize.height + textHeight * 0.7) / 2).roundToInt()
        )

        g.font = Font("Dialog", Font.PLAIN, 22)

        val message2 = Game.deathMessage
        val textWidth2 = g.fontMetrics.stringWidth(message2)
        val textHeight2 = g.fontMetrics.height

        g.color = Color(230, 230, 230)
        g.drawString(
            message2,
            (Game.board.windowSize.width - textWidth2) / 2,
            textHeight + ((Game.board.windowSize.height + textHeight2 * 0.7) / 2).roundToInt()
        )
    }
}