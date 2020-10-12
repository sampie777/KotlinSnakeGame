package nl.sajansen.kotlinsnakegame.objects.visuals

import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.gui.utils.drawImageInXCenter
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.*
import java.awt.image.BufferedImage

object GameOverlay {

    fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(Game.board.windowSize.width, Game.board.windowSize.height)

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

}