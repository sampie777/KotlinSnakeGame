package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents


import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.math.roundToInt

class CheckBox : ClickableComponent {
    private val logger = Logger.getLogger(CheckBox::class.java.name)

    override var position: Point = Point(0, 0)
    override var size: Dimension = Dimension(42, 42)
    override var componentAlignmentX = ComponentAlignment.LEFT
    override var componentAlignmentY = ComponentAlignment.TOP
    var cornerRounding = Dimension(30, 10)
    var lineWidth = 2F
    var fontColor = Color.WHITE
    var backgroundColor = Color(57, 72, 92)
    var isChecked = true
        set(value) {
            val oldValue = field
            field = value
            if (oldValue != value) {
                onChange?.invoke(value)
            }
        }

    var onClick: ((e: MouseEvent?) -> Unit)? = null
    var onChange: ((isChecked: Boolean) -> Unit)? = null

    override fun click(e: MouseEvent?) {
        isChecked = !isChecked
        onClick?.invoke(e)
    }

    override fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(size.width, size.height)

        g.stroke = BasicStroke(lineWidth)
        g.color = backgroundColor

        g.fillRoundRect(
            lineWidth.toInt(),
            lineWidth.toInt(),
            size.width - 2 * lineWidth.toInt(),
            size.height - 2 * lineWidth.toInt(),
            cornerRounding.height,
            cornerRounding.width
        )

        g.color = fontColor
        g.drawRoundRect(
            lineWidth.toInt(),
            lineWidth.toInt(),
            size.width - 2 * lineWidth.toInt(),
            size.height - 2 * lineWidth.toInt(),
            cornerRounding.height,
            cornerRounding.width
        )

        drawCheckedStatus(g)

        g.dispose()
        return bufferedImage
    }

    private fun drawCheckedStatus(g: Graphics2D) {
        if (!isChecked) {
            return
        }

        val width = (size.width * 0.7).roundToInt()
        val height = (size.height * 0.7).roundToInt()

        g.color = fontColor
        g.fillRoundRect(
            lineWidth.toInt() + ((size.width - lineWidth.toInt()) - width) / 2,
            lineWidth.toInt() + ((size.height - lineWidth.toInt()) - height) / 2,
            width,
            height,
            cornerRounding.height,
            cornerRounding.width
        )
    }
}