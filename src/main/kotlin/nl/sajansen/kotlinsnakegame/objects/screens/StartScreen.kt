package nl.sajansen.kotlinsnakegame.objects.screens


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.exitApplication
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.getAvailableColorForSnake
import nl.sajansen.kotlinsnakegame.objects.player.HumanPlayer
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Button
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Point
import java.util.logging.Logger

object StartScreen : Screen(), KeyEventListener {
    private val logger = Logger.getLogger(StartScreen::class.java.name)

    override var backgroundOpacity = 255

    init {
        val titleLabel = Label(Config.titleMessage)
        titleLabel.font = Font("Dialog", Font.PLAIN, 36)
        titleLabel.position = Point(0, 70)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        add(titleLabel)

        val singlePlayerButton = Button("Snake single player")
        singlePlayerButton.position = Point(0, 200)
        singlePlayerButton.size = Dimension(370, 0)
        singlePlayerButton.cornerRounding = Dimension(30, 30)
        singlePlayerButton.backgroundColor = Color(100, 100, 100)
        singlePlayerButton.font = Font("Dialog", Font.PLAIN, 28)
        singlePlayerButton.componentAlignmentX = ComponentAlignment.CENTER
        singlePlayerButton.onClick = { startSinglePlayer() }
        add(singlePlayerButton)

        val snakeVsSnakeButton = Button("Snake vs. Snake")
        snakeVsSnakeButton.position = Point(0, 270)
        snakeVsSnakeButton.size = Dimension(370, 0)
        snakeVsSnakeButton.cornerRounding = Dimension(30, 30)
        snakeVsSnakeButton.backgroundColor = Color(100, 100, 100)
        snakeVsSnakeButton.font = Font("Dialog", Font.PLAIN, 28)
        snakeVsSnakeButton.componentAlignmentX = ComponentAlignment.CENTER
        snakeVsSnakeButton.onClick = { startSnakeVsSnake() }
        add(snakeVsSnakeButton)

        val snakeVsGnomeButton = Button("Snake vs. Gnome")
        snakeVsGnomeButton.position = Point(0, 340)
        snakeVsGnomeButton.size = Dimension(370, 0)
        snakeVsGnomeButton.cornerRounding = Dimension(30, 30)
        snakeVsGnomeButton.backgroundColor = Color(100, 100, 100)
        snakeVsGnomeButton.font = Font("Dialog", Font.PLAIN, 28)
        snakeVsGnomeButton.componentAlignmentX = ComponentAlignment.CENTER
        snakeVsGnomeButton.onClick = { startSnakeVsGnome() }
        add(snakeVsGnomeButton)

        val freePlayButton = Button("Free Play")
        freePlayButton.position = Point(0, 410)
        freePlayButton.size = Dimension(370, 0)
        freePlayButton.cornerRounding = Dimension(30, 30)
        freePlayButton.backgroundColor = Color(100, 100, 100)
        freePlayButton.font = Font("Dialog", Font.PLAIN, 28)
        freePlayButton.componentAlignmentX = ComponentAlignment.CENTER
        freePlayButton.onClick = { startFreePlay() }
        add(freePlayButton)

        val quitButton = Button("Quit")
        quitButton.position = Point(0, 500)
        quitButton.backgroundColor = null
        quitButton.font = Font("Dialog", Font.PLAIN, 16)
        quitButton.componentAlignmentX = ComponentAlignment.CENTER
        quitButton.onClick = { exitApplication() }
        add(quitButton)

        GameOverlay.addControlsOverlay(this)
    }

    private fun startSinglePlayer() {
        if (Game.players.isEmpty()) {
            logger.info("Adding player")
            val player1 = SnakePlayer()
            Game.addPlayer(player1)
        } else {
            val player = Game.players.first()
            Game.players.removeAll { it != player }

            if (player !is SnakePlayer) {
                logger.info("Converting player $player to SnakePlayer")
                Game.remove(player)

                val snakePlayer = player.changeTo(SnakePlayer::class.java)
                Game.addPlayer(snakePlayer)
            }
        }

        Game.restart()
    }

    private fun startSnakeVsSnake() {
        if (Game.players.isEmpty()) {
            logger.info("Adding players")
            val player1 = SnakePlayer()
            Game.addPlayer(player1)
        }

        if (Game.players.size == 1) {
            logger.info("Adding second player")
            val player2 = SnakePlayer()
            player2.setControls(
                up = Config.player2UpKey,
                right = Config.player2RightKey,
                down = Config.player2DownKey,
                left = Config.player2LeftKey
            )
            getAvailableColorForSnake(player2)
            Game.addPlayer(player2)
        }

        Game.players.toTypedArray().forEach { player ->
            if (player is SnakePlayer) return@forEach

            logger.info("Converting player $player to SnakePlayer")
            Game.remove(player)

            val snakePlayer = player.changeTo(SnakePlayer::class.java)
            Game.addPlayer(snakePlayer)

            getAvailableColorForSnake(snakePlayer)
        }

        Game.restart()
    }

    private fun startSnakeVsGnome() {
        if (Game.players.isEmpty()) {
            logger.info("Adding players")
            val player1 = HumanPlayer()
            Game.addPlayer(player1)
        }

        if (Game.players.size == 1) {
            logger.info("Adding second player")

            val player2 = SnakePlayer()
            player2.setControls(
                up = Config.player2UpKey,
                right = Config.player2RightKey,
                down = Config.player2DownKey,
                left = Config.player2LeftKey
            )
            getAvailableColorForSnake(player2)
            Game.addPlayer(player2)
        }

        Game.players.toTypedArray()
            .forEachIndexed { index, player ->
                if (index == 0) {
                    if (player is HumanPlayer) return@forEachIndexed
                    Game.remove(player)
                    Game.addPlayer(player.changeTo(HumanPlayer::class.java))
                }
                if (player is SnakePlayer) return@forEachIndexed

                logger.info("Converting player $player to SnakePlayer")
                Game.remove(player)

                val snakePlayer = player.changeTo(SnakePlayer::class.java)
                Game.addPlayer(snakePlayer)

                getAvailableColorForSnake(snakePlayer)
            }

        Game.restart()
    }

    private fun startFreePlay() {
        Game.restart()
    }
}