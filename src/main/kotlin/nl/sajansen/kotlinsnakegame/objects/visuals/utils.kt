package nl.sajansen.kotlinsnakegame.objects.visuals

import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Color
import java.awt.Graphics2D


fun paintBackground(g: Graphics2D, transparency: Int = 255) {
    g.color = Color(83, 83, 83, transparency)
    g.fillRect(0, 0, Game.board.windowSize.width, Game.board.windowSize.height)
}