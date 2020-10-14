package nl.sajansen.kotlinsnakegame.objects.visuals

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.gui.utils.drawImageInXCenter
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import java.awt.*
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

object GameOverlay {

    fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.windowSize.width, Game.board.windowSize.height)

        // painting stuff
        when (Game.state.runningState) {
            GameRunningState.RESET -> {
                paintPressKeyToStartScreen(g)
                g.dispose()
                return bufferedImage
            }
            GameRunningState.ENDED -> {
                paintGameOverScreen(g)
                g.dispose()
                return bufferedImage
            }
            GameRunningState.PAUSED -> paintGamePausedScreen(g)
            else -> {
            }
        }

        drawImageInXCenter(g, 0, bufferedImage.width, paintScoreOverlay())

        g.dispose()
        return bufferedImage
    }

    private fun paintScoreOverlay(): BufferedImage {
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

    private fun paintPressKeyToStartScreen(g: Graphics2D) {
        paintBackground(g)

        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.pressKeyToStartMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(message, (Game.board.windowSize.width - textWidth) / 2, ((Game.board.windowSize.height+ textHeight * 0.7) / 2).roundToInt())
    }

    private fun paintGamePausedScreen(g: Graphics2D) {
        paintBackground(g, 200)

        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.gamePausedMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(message, (Game.board.windowSize.width - textWidth) / 2, ((Game.board.windowSize.height + textHeight * 0.7) / 2).roundToInt())
    }

    private fun paintGameOverScreen(g: Graphics2D) {
        paintBackground(g, 200)

        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.gameOverMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(message, (Game.board.windowSize.width - textWidth) / 2, ((Game.board.windowSize.height + textHeight * 0.7) / 2).roundToInt())

        g.font = Font("Dialog", Font.PLAIN, 22)

        val message2 = Game.deathMessage
        val textWidth2 = g.fontMetrics.stringWidth(message2)
        val textHeight2 = g.fontMetrics.height

        g.color = Color(230, 230, 230)
        g.drawString(message2, (Game.board.windowSize.width - textWidth2) / 2, textHeight + ((Game.board.windowSize.height + textHeight2 * 0.7) / 2).roundToInt())
    }

    private fun paintBackground(g: Graphics2D, transparency: Int = 255) {
        g.color = Color(83, 83, 83, transparency)
        g.fillRect(0, 0, Game.board.windowSize.width, Game.board.windowSize.height)
    }

}