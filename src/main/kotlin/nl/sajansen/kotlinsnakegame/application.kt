package nl.sajansen.kotlinsnakegame

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.mainFrame.MainFrame
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.player.HumanPlayer
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

    val player = HumanPlayer()
//    val player = SnakePlayer()

    val player2 = SnakePlayer(
        name = "Player 2",
        color = Color(0, 0, 255, 100),
    )
    player2.setControls(
        up = Config.player2UpKey,
        right = Config.player2RightKey,
        down = Config.player2DownKey,
        left = Config.player2LeftKey
    )

    Game.addPlayer(player)
    Game.addPlayer(player2)
}