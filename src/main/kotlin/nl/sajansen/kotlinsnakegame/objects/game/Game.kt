package nl.sajansen.kotlinsnakegame.objects.game

import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.objects.GameObject
import nl.sajansen.kotlinsnakegame.objects.board.Board
import nl.sajansen.kotlinsnakegame.objects.player.Player
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

object Game : KeyEventListener, GameObject {
    private val logger = Logger.getLogger(Game::class.java.name)

    var state = GameState()
    var players = arrayListOf<Player>()
    var board = Board()

    init {
        EventHub.register(this)
    }

    fun restart() {
        logger.info("Restarting game")
        resetState()
        reset()

        GameStepTimer.restart()

        state.runningState = GameRunningState.STARTED
        logger.info("Game has started")
    }

    private fun resetState() {
        state = GameState()
    }

    override fun keyReleased(e: KeyEvent) {
        if (state.runningState == GameRunningState.RESET) {
            // Restart game on any key press
            restart()
            return
        }

        if (state.runningState == GameRunningState.STARTED || state.runningState == GameRunningState.PAUSED) {
            if (e.keyCode == KeyEvent.VK_P || e.keyCode == KeyEvent.VK_PAUSE) {
                pause()
            }
        }
    }

    private fun pause() {
        if (state.runningState == GameRunningState.PAUSED) {
            logger.info("Unpausing game")
            GameStepTimer.restart()
            state.runningState = GameRunningState.STARTED
        } else {
            logger.info("Pausing game")
            GameStepTimer.stop()
            state.runningState = GameRunningState.PAUSED
        }
    }

    fun addPlayer(): Player {
        return addPlayer(Player())
    }

    fun addPlayer(player: Player): Player {
        players.add(player)
        return player
    }

    override fun reset() {
        board.reset()
    }

    override fun step() {
        board.step()
    }

    override fun paint(): BufferedImage {
        return board.paint()
    }
}