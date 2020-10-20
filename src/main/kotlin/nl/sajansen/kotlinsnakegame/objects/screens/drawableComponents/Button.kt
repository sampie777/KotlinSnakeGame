package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents


import nl.sajansen.kotlinsnakegame.events.KeyEventListener
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger

class Button(override var text: String = "") : ClickableComponent, Label(text), KeyEventListener {
    private val logger = Logger.getLogger(Button::class.java.name)

    override var margin = Dimension(50, 12)
    override var textAlignmentX = ComponentAlignment.CENTER
    var cornerRounding = Dimension(30, 10)
    var lineWidth = 1F
    var backgroundColor: Color? = Color(57, 72, 92)

    var onClick: ((e: MouseEvent?) -> Unit)? = null
    var hotKey: KeyEvent? = null

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

    override fun keyReleased(e: KeyEvent) {
        if (hotKey == null) return

        if (isHotkey(e)) {
            click(null)
        }
    }

    fun isHotkey(e: KeyEvent) = e.keyCode == hotKey!!.keyCode && e.modifiers == hotKey!!.modifiers
}