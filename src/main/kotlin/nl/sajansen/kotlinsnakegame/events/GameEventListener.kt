package nl.sajansen.kotlinsnakegame.events

interface GameEventListener : EventListener {
    fun runningStateChanged() {}
}