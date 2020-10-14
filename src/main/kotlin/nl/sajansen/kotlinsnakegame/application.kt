package nl.sajansen.kotlinsnakegame

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.mainFrame.MainFrame
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import java.awt.Color
import java.awt.EventQueue
import java.util.logging.Logger

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    val logger = Logger.getLogger("Application")
    logger.info("Starting application ${ApplicationInfo.artifactId}:${ApplicationInfo.version}")
    logger.info("Executing JAR directory: " + getCurrentJarDirectory(ApplicationInfo).absolutePath)

    EventQueue.invokeLater {
        MainFrame.createAndShow()
    }

//    val player = HumanPlayer()
    val player = SnakePlayer(
        name = "Player 1",
    )
    player.setRandomStartPosition()
    player.setControls(
        up = Config.player1UpKey,
        right = Config.player1RightKey,
        down = Config.player1DownKey,
        left = Config.player1LeftKey
    )

    val player2 = SnakePlayer(
        name = "Player 2",
        color = Color(0, 0, 255, 100),
    )
    player2.setRandomStartPosition()
    player2.setControls(
        up = Config.player2UpKey,
        right = Config.player2RightKey,
        down = Config.player2DownKey,
        left = Config.player2LeftKey
    )

    Game.addPlayer(player)
    Game.addPlayer(player2)
}