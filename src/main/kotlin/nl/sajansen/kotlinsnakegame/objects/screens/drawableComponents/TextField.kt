package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

class TextField(override var text: String = "") : FocusListener, Label(text), KeyEventListener {
    private val logger = Logger.getLogger(TextField::class.java.name)

    override var size: Dimension = Dimension(200, 0)
    override var margin = Dimension(15, 10)
    var cornerRounding = Dimension(10, 10)
    var lineWidth = 1F
    var backgroundColor: Color? = Color(92, 92, 92)
    var maxLength: Int? = null

    var onChange: ((value: String) -> Unit)? = null
    private var isActivate = false
    private var cursorPosition = text.length
    private var cursorBlinkTimeCounter = 0
    private var isCursorLit = true

    override fun keyPressed(e: KeyEvent) {
        if (!isActivate) {
            return
        }

        when (e.keyCode) {
            KeyEvent.VK_BACK_SPACE -> {
                if (cursorPosition >= text.length) {
                    text = text.dropLast(1)
                    cursorPosition = text.length
                } else if (cursorPosition <= 0) {
                    cursorPosition = 0
                } else {
                    text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition)
                    cursorPosition--
                }

                onChange?.invoke(text)
            }
            KeyEvent.VK_LEFT -> cursorPosition--
            KeyEvent.VK_RIGHT -> cursorPosition++
            KeyEvent.VK_HOME -> cursorPosition = 0
            KeyEvent.VK_END -> cursorPosition = text.length
        }
    }

    override fun keyTyped(e: KeyEvent) {
        if (!isActivate) {
            return
        }

        // Filter out SOME invalid keys
        if (e.keyChar.toInt() < KeyEvent.VK_SPACE || e.keyChar.toInt() > KeyEvent.VK_UNDERSCORE) {
            return
        }

        if (maxLength != null && text.length >= maxLength!!) {
            logger.info("Max length reached: $maxLength")
            return
        }

        if (cursorPosition >= text.length) {
            text += e.keyChar
            cursorPosition = text.length
        } else if (cursorPosition <= 0) {
            text = e.keyChar + text
            cursorPosition = e.keyChar.toString().length
        } else {
            text = text.substring(0, cursorPosition) + e.keyChar + text.substring(cursorPosition)
            cursorPosition++
        }

        onChange?.invoke(text)
    }

    private fun showActiveState() {
        if (isActivate) {
            backgroundColor = Color(122, 122, 122)
        } else {
            backgroundColor = Color(92, 92, 92)
        }
    }

    override fun paint(): BufferedImage {
        if (size.height == 0) {
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
        paintCursor(g)

        g.dispose()
        return bufferedImage
    }

    override fun calculateSize() {
        val bufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = bufferedImage.createGraphics()
        g.font = font
        val textHeight = g.fontMetrics.height

        size = Dimension(size.width, textHeight + 2 * margin.height)

        if (componentAlignmentX == ComponentAlignment.CENTER) {
            position.x = (Game.board.windowSize.width - size.width) / 2
        }
        if (componentAlignmentY == ComponentAlignment.CENTER) {
            position.y = (Game.board.windowSize.height - size.height) / 2
        }
    }

    private fun paintCursor(g: Graphics2D) {
        if (!isActivate) return

        if (++cursorBlinkTimeCounter > Config.maxFps / 2) {
            isCursorLit = !isCursorLit
            cursorBlinkTimeCounter = 0
        }

        if (!isCursorLit) {
            return
        }

        if (cursorPosition > text.length) {
            cursorPosition = text.length
        } else if (cursorPosition < 0) {
            cursorPosition = 0
        }

        val textWidth = g.fontMetrics.stringWidth(text.substring(0, cursorPosition))
        val textHeight = g.fontMetrics.height
        val cursorX = margin.width + textWidth

        g.color = fontColor
        g.stroke = BasicStroke(1F)

        g.drawLine(cursorX, margin.height, cursorX, margin.height + textHeight)
    }

    override fun focusGained(e: FocusEvent?) {
        isActivate = true
        isCursorLit = true
        showActiveState()
    }

    override fun focusLost(e: FocusEvent?) {
        isActivate = false
        showActiveState()
    }
}