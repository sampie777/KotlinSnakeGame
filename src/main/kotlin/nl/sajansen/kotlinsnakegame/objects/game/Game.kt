package nl.sajansen.kotlinsnakegame.objects.game

import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.GameObject
import nl.sajansen.kotlinsnakegame.objects.board.Board
import nl.sajansen.kotlinsnakegame.objects.player.Player
import nl.sajansen.kotlinsnakegame.objects.visuals.GameOverlay
import java.awt.Graphics2D
import java.awt.event.ActionEvent
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

        if (e.keyCode == KeyEvent.VK_PAUSE) {
            pause()
        }
    }

    fun pause() {
        if (state.runningState == GameRunningState.PAUSED) {
            logger.info("Unpausing game")
            GameStepTimer.restart()
            state.runningState = GameRunningState.STARTED
        } else if (state.runningState == GameRunningState.STARTED) {
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

    fun end(reason: String) {
        logger.info("Game ended: $reason")
        state.runningState = GameRunningState.ENDED
    }

    override fun reset() {
        board.reset()
    }

    override fun step() {
        board.step()
    }

    override fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(board.visibleSize.width, board.visibleSize.height)

        g.drawImage(board.paint(), null, 0, 0)
        g.drawImage(GameOverlay.paint(), null, 0, 0)

        g.dispose()
        return bufferedImage
    }
}