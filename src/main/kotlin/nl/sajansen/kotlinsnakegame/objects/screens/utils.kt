package nl.sajansen.kotlinsnakegame.objects.screens

import nl.sajansen.kotlinsnakegame.createKeyEvent
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Button
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent


fun paintBackground(g: Graphics2D, transparency: Int = 255) {
    g.color = Color(83, 83, 83, transparency)
    g.fillRect(0, 0, Game.board.windowSize.width, Game.board.windowSize.height)
}

fun Screen.backButton(text: String = "Back"): Button {
    val backButton = Button(text)
    backButton.backgroundColor = null
    backButton.position = Point(10, 10)
    backButton.hotKey = createKeyEvent(KeyEvent.VK_ESCAPE)
    backButton.onClick = {
        close()
    }
    return backButton
}