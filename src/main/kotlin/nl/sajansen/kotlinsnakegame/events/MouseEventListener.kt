package nl.sajansen.kotlinsnakegame.events


import java.awt.event.MouseEvent

interface MouseEventListener : EventListener {
    fun mouseClicked(e: MouseEvent) {
    }

    fun mousePressed(e: MouseEvent) {
    }

    fun mouseReleased(e: MouseEvent) {
    }

    fun mouseEnteredScreen(e: MouseEvent) {
    }

    fun mouseExitedScreen(e: MouseEvent) {
    }
}