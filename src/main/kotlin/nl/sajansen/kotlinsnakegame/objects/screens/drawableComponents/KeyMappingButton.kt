package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents


import nl.sajansen.kotlinsnakegame.createKeyEvent
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.keyEventToString
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

class KeyMappingButton(private var keyEvent: KeyEvent? = null) : ClickableComponent, Label(""), KeyEventListener {
    private val logger = Logger.getLogger(KeyMappingButton::class.java.name)

    override var margin = Dimension(50, 12)
    override var textAlignmentX = ComponentAlignment.CENTER
    var cornerRounding = Dimension(30, 10)
    var lineWidth = 2F
    var backgroundColor: Color? = Color(74, 84, 97)

    var allowEmpty = true
    var onClick: ((e: MouseEvent?) -> Unit)? = { calibrate() }
    var onSave: ((value: KeyEvent?) -> Unit)? = null

    private val keyEventListener = KeyEventButtonKeyListener()
    private var isCalibrating: Boolean = false

    constructor(keyCode: Int) : this(createKeyEvent(keyCode))

    init {
        setButtonText()
    }

    override fun click(e: MouseEvent?) {
        onClick?.invoke(e)
    }

    override fun paint(): BufferedImage {
        if (size == Dimension(0, 0)) {
            calculateSize()
        }

        val (bufferedImage, g: Graphics2D) = createGraphics(size.width, size.height)

        g.stroke = BasicStroke(lineWidth)
        if (backgroundColor != null) {
            g.color = backgroundColor!!

            g.fillRoundRect(
                lineWidth.toInt(),
                lineWidth.toInt(),
                size.width - 2 * lineWidth.toInt(),
                size.height - 2 * lineWidth.toInt(),
                cornerRounding.height,
                cornerRounding.width
            )
        }

        g.color = fontColor
        g.drawRoundRect(
            lineWidth.toInt(),
            lineWidth.toInt(),
            size.width - 2 * lineWidth.toInt(),
            size.height - 2 * lineWidth.toInt(),
            cornerRounding.height,
            cornerRounding.width
        )

        drawStringOnImage(bufferedImage, g)

        g.dispose()
        return bufferedImage
    }

    private fun setButtonText() {
        if (keyEvent == null) {
            text = "Click to Assign..."
        } else {
            text = keyEventToString(keyEvent)
        }
    }

    private fun calibrate() {
        if (keyEventListener.isCalibrating) {
            keyEventListener.cancelCalibration()
            setButtonText()
            return
        }

        // Clear key
        if (allowEmpty && keyEvent != null) {
            keyEvent = null
            setButtonText()
            onSave?.invoke(keyEvent)
            return
        }

        text = "..."
        keyEventListener.startCalibration { keyEvent ->
            logger.info("Calibrated key event: ${keyEventToString(keyEvent)}")

            if (keyEvent.keyCode == KeyEvent.VK_ESCAPE) {
                logger.info("Ignoring Escape key for calibration")
            } else {
                this.keyEvent = keyEvent
                onSave?.invoke(keyEvent)
            }

            setButtonText()
        }
    }

    override fun keyReleased(e: KeyEvent) {
        keyEventListener.processKeyEvent(e)
    }
}

private class KeyEventButtonKeyListener : KeyEventListener {
    private val logger = Logger.getLogger(KeyEventButtonKeyListener::class.java.name)

    var isCalibrating: Boolean = false
    private var calibrationCallback : ((KeyEvent) -> Unit)? = null

    override fun keyReleased(e: KeyEvent) {
        processKeyEvent(e)
    }

    fun processKeyEvent(e: KeyEvent) {
        if (!isCalibrating) return

        processCalibration(e)
    }

    private fun processCalibration(e: KeyEvent) {
        isCalibrating = false

        if (calibrationCallback == null) {
            logger.warning("Cannot invoke calibrationCallback because callback is null")
            return
        }

        calibrationCallback?.invoke(e)
    }

    fun startCalibration(callback: (keyEvent: KeyEvent) -> Unit) {
        calibrationCallback = callback
        isCalibrating = true
    }

    fun cancelCalibration() {
        logger.info("Cancelling calibration")
        calibrationCallback = null
        isCalibrating = false
    }
}