package nl.sajansen.kotlinsnakegame.gui.mainFrame


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.utils.drawImageInXYCenter
import nl.sajansen.kotlinsnakegame.gui.utils.setDefaultRenderingHints
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.game.GameRunningState
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.logging.Logger
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.roundToInt

class MainFramePanel : JPanel() {
    private val logger = Logger.getLogger(MainFramePanel::class.java.name)

    private val screenUpdateTimer: Timer

    @Volatile
    private var isRepainting = false

    init {
        initGui()

        logger.info("Scheduling repaint timer every ${1000 / Config.paintFPS} milliseconds")
        screenUpdateTimer = Timer((1000 / Config.paintFPS).toInt()) {
            screenUpdateTimerStep()
        }
        screenUpdateTimer.start()
    }

    private fun initGui() {
        preferredSize = Game.board.windowSize
    }

    private fun screenUpdateTimerStep() {
        if (isRepainting) {
            logger.finer("Skipping paint update: still updating")
            return
        }

        repaint()
    }

    fun stopScreenUpdateTimer() {
        screenUpdateTimer.stop()
    }

    override fun paintComponent(g: Graphics) {
        isRepainting = true
        super.paintComponents(g as Graphics2D)
        setDefaultRenderingHints(g)

        paintPanel(g)

        isRepainting = false
    }

    private fun paintPanel(g: Graphics2D) {
        paintBackground(g)

        // painting stuff
        when (Game.state.runningState) {
            GameRunningState.RESET -> return paintPressKeyToStartScreen(g)
            GameRunningState.PAUSED -> paintGamePausedScreen(g)
            GameRunningState.ENDED -> return paintGameOverScreen(g)
            else -> {
            }
        }
        drawImageInXYCenter(g, width, height, Game.paint())
    }

    private fun paintBackground(g: Graphics2D) {
        g.color = Color(83, 83, 83)
        g.fillRect(0, 0, width, height)
    }

    private fun paintPressKeyToStartScreen(g: Graphics2D) {
        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.pressKeyToStartMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(message, (width - textWidth) / 2, ((height + textHeight * 0.7) / 2).roundToInt())
    }

    private fun paintGamePausedScreen(g: Graphics2D) {
        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.gamePausedMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(message, (width - textWidth) / 2, ((height + textHeight * 0.7) / 2).roundToInt())
    }

    private fun paintGameOverScreen(g: Graphics2D) {
        g.font = Font("Dialog", Font.PLAIN, 30)

        val message = Config.gameOverMessage
        val textWidth = g.fontMetrics.stringWidth(message)
        val textHeight = g.fontMetrics.height

        g.color = Color.WHITE
        g.drawString(message, (width - textWidth) / 2, ((height + textHeight * 0.7) / 2).roundToInt())

        g.font = Font("Dialog", Font.PLAIN, 22)

        val message2 = Game.deathMessage
        val textWidth2 = g.fontMetrics.stringWidth(message2)
        val textHeight2 = g.fontMetrics.height

        g.color = Color(230, 230, 230)
        g.drawString(message2, (width - textWidth2) / 2, textHeight + ((height + textHeight2 * 0.7) / 2).roundToInt())
    }
}