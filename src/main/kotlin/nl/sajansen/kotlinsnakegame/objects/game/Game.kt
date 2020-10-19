package nl.sajansen.kotlinsnakegame.objects.game

import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.board.Board
import nl.sajansen.kotlinsnakegame.objects.board.DefaultBoard
import nl.sajansen.kotlinsnakegame.objects.player.Player
import nl.sajansen.kotlinsnakegame.objects.screens.GameOverScreen
import nl.sajansen.kotlinsnakegame.objects.screens.GameOverlay
import nl.sajansen.kotlinsnakegame.objects.screens.PauseScreen
import nl.sajansen.kotlinsnakegame.objects.screens.ScreenManager
import nl.sajansen.kotlinsnakegame.objects.sound.SoundPlayer
import nl.sajansen.kotlinsnakegame.objects.sound.Sounds
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

object Game : KeyEventListener {
    private val logger = Logger.getLogger(Game::class.java.name)

    var state = GameState()
    var players = arrayListOf<Player>()
    var board: Board = DefaultBoard()
    var deathMessage = ""

    init {
        EventHub.register(this)
    }

    fun restart() {
        logger.info("Restarting game")
        resetState()
        reset()

        GameStepTimer.restart()

        ScreenManager.closeAll()
        GameOverlay.show()

        state.runningState = GameRunningState.STARTED
        logger.info("Game has started")
    }

    private fun resetState() {
        state = GameState()
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_PAUSE) {
            togglePause()
        }
    }

    fun togglePause() {
        if (state.runningState == GameRunningState.PAUSED) {
            unpause()
        } else if (state.runningState == GameRunningState.STARTED) {
            pause()
        }
    }

    fun pause() {
        if (state.runningState != GameRunningState.STARTED) {
            logger.info("Can't pause game. Game isn't even running")
            return
        }

        logger.info("Pausing game")
        GameStepTimer.stop()
        state.runningState = GameRunningState.PAUSED
        PauseScreen.show()
    }

    fun unpause() {
        if (state.runningState != GameRunningState.PAUSED) {
            logger.info("Can't unpause game. Game isn't paused")
            return
        }

        logger.info("Unpausing game")
        GameStepTimer.restart()
        state.runningState = GameRunningState.STARTED
        PauseScreen.close()
    }

    fun addPlayer(player: Player): Player {
        players.add(player)
        return player
    }

    fun end(reason: String, sound: Sounds = Sounds.GAME_END) {
        logger.info("Game ended: $reason")
        deathMessage = reason
        state.runningState = GameRunningState.ENDED
        SoundPlayer.play(sound)

        GameStepTimer.stop()
        GameOverScreen.show()
    }

    fun reset() {
        board.reset()
    }

    fun step() {
        state.time++
        board.step()
    }

    fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(board.windowSize.width, board.windowSize.height)

        g.drawImage(board.paint(), null, 0, 0)
        g.drawImage(ScreenManager.paint(), null, 0, 0)

        g.dispose()
        return bufferedImage
    }
}