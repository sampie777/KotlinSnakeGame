package nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents

import java.awt.event.MouseEvent

interface ClickableComponent : DrawableComponent {
    fun click(e: MouseEvent)
    fun mousePressed(e: MouseEvent) {}
}