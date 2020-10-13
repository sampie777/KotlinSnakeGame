package nl.sajansen.kotlinsnakegame.events

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

interface KeyEventListener : EventListener, KeyListener {

    override fun keyTyped(e: KeyEvent) {}

    override fun keyPressed(e: KeyEvent) {}

    override fun keyReleased(e: KeyEvent) {}
}