package nl.sajansen.kotlinsnakegame

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.mainFrame.MainFrame
import nl.sajansen.kotlinsnakegame.objects.screens.StartScreen
import java.awt.EventQueue
import java.util.logging.Logger

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    val logger = Logger.getLogger("Application")
    logger.info("Starting application ${ApplicationInfo.artifactId}:${ApplicationInfo.version}")
    logger.info("Executing JAR directory: " + getCurrentJarDirectory(ApplicationInfo).absolutePath)

//    Game.addPlayer(HumanPlayer())
////    Game.addPlayer(HumanPlayer(name = "Henk"))
//    Game.addPlayer(SnakePlayer())
//
//    Server.start()
//
//    val l: Long = 1000
//    Thread.sleep(l)
//
//    val client = RemoteServer()
//    Thread.sleep(l)
//    println("")
//    Server.sendGameData()
//    Thread.sleep(l)
//    println("")
//    client.stop()
//    Server.stop()
//
//    client.join()
//    Server.join()
//    return

    if ("--virtualConfig" !in args) {
        Config.enableWriteToFile(true)
    }
    Config.load()
    Config.save()

//    MultiPlayer.startAsServer()

    EventQueue.invokeLater {
        MainFrame.create()
        StartScreen.show()
        MainFrame.show()
    }
}