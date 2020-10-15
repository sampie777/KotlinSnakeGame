package nl.sajansen.kotlinsnakegame.gui.mainFrame


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.utils.drawImageInXYCenter
import nl.sajansen.kotlinsnakegame.gui.utils.setDefaultRenderingHints
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.logging.Logger
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.min

class MainFramePanel : JPanel() {
    private val logger = Logger.getLogger(MainFramePanel::class.java.name)

    private val screenUpdateTimer: Timer

    @Volatile
    private var isRepainting = false

    init {
        initGui()

        // Make sure we don't ask more than useful of the painting cycle
        val fps = min(Config.maxFps, Config.stepPerSeconds)

        logger.info("Scheduling repaint timer every ${1000 / fps} milliseconds")
        screenUpdateTimer = Timer((1000 / fps).toInt()) {
            screenUpdateTimerStep()
        }
        screenUpdateTimer.start()

        addMouseListener(MainFramePanelMouseListener())
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

        drawImageInXYCenter(g, width, height, Game.paint())
    }

    private fun paintBackground(g: Graphics2D) {
        g.color = Color(83, 83, 83)
        g.fillRect(0, 0, width, height)
    }
}