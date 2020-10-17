package nl.sajansen.kotlinsnakegame.objects.visuals.drawableComponents

import java.awt.event.MouseEvent

interface ClickableComponent : DrawableComponent {
    fun click(e: MouseEvent)
}