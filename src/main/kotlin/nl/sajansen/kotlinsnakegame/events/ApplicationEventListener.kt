package nl.sajansen.kotlinsnakegame.events

interface ApplicationEventListener : EventListener {
    fun onShutDown() {}
}