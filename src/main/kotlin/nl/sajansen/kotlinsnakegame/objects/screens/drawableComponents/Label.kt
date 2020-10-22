package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents


import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.*
import java.awt.image.BufferedImage
import java.util.logging.Logger

open class Label(open var text: String = "") : DrawableComponent {
    private val logger = Logger.getLogger(Label::class.java.name)

    override var position: Point = Point(0, 0)
    override var size: Dimension = Dimension(0, 0)
    open var font = Font("Dialog", Font.PLAIN, 20)
    open var fontColor = Color.WHITE
    open var margin = Dimension(15, 5)
    open var textAlignmentX = ComponentAlignment.LEFT
    override var componentAlignmentX = ComponentAlignment.LEFT
    override var componentAlignmentY = ComponentAlignment.TOP
    override var isVisible = true

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

    protected open fun calculateSize() {
        val bufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = bufferedImage.createGraphics()
        g.font = font
        val textWidth = g.fontMetrics.stringWidth(text)
        val textHeight = g.fontMetrics.height

        if (size.width == 0) {
            size.width = textWidth + 2 * margin.width
        }
        if (size.height == 0) {
            size.height = textHeight + 2 * margin.height
        }

        if (componentAlignmentX == ComponentAlignment.CENTER) {
            position.x = (Game.board.windowSize.width - size.width) / 2
        }
        if (componentAlignmentY == ComponentAlignment.CENTER) {
            position.y = (Game.board.windowSize.height - size.height) / 2
        }
    }
}