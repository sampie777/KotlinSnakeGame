package nl.sajansen.kotlinsnakegame

import nl.sajansen.kotlinsnakegame.gui.mainFrame.MainFrame
import nl.sajansen.kotlinsnakegame.objects.game.Game
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

    val player1 = Game.addPlayer()
    player1.name = "Player 1"
}