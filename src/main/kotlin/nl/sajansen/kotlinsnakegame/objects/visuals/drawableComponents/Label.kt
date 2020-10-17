package nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents


import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import java.awt.*
import java.awt.image.BufferedImage
import java.util.logging.Logger

open class Label(open val text: String = "") : DrawableComponent {
    private val logger = Logger.getLogger(Label::class.java.name)

    override var position: Point = Point(0, 0)
    override var size: Dimension = Dimension(0, 0)
    open var font = Font("Dialog", Font.PLAIN, 20)
    open var fontColor = Color.WHITE
    open var margin = Dimension(15, 5)
    open var textAlignmentX = ComponentAlignment.LEFT
    override var componentAlignmentX = ComponentAlignment.LEFT
    override var componentAlignmentY = ComponentAlignment.TOP

    override fun paint(): BufferedImage {
        if (size == Dimension(0, 0)) {
            calculateSize()
        }

        val (bufferedImage, g: Graphics2D) = createGraphics(size.width, size.height)

        drawStringOnImage(bufferedImage, g)

        g.dispose()
        return bufferedImage
    }

    protected fun drawStringOnImage(bufferedImage: BufferedImage, g: Graphics2D) {
        g.font = font
        g.color = fontColor

        val textWidth = g.fontMetrics.stringWidth(text)
        val textPositionY = g.fontMetrics.ascent + margin.height

        when (textAlignmentX) {
            ComponentAlignment.CENTER -> g.drawString(text, (bufferedImage.width - textWidth) / 2, textPositionY)
            ComponentAlignment.RIGHT -> g.drawString(
                text,
                bufferedImage.width - textWidth - margin.width,
                textPositionY
            )
            else -> g.drawString(text, margin.width, textPositionY)
        }
    }

    protected fun calculateSize() {
        val bufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = bufferedImage.createGraphics()
        g.font = font
        val textWidth = g.fontMetrics.stringWidth(text)
        val textHeight = g.fontMetrics.height

        size = Dimension(textWidth + 2 * margin.width, textHeight + 2 * margin.height)
    }
}