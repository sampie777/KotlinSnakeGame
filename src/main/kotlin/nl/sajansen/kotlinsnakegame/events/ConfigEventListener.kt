package nl.sajansen.kotlinsnakegame.events


interface ConfigEventListener : EventListener {
    fun propertyUpdated(name: String, value: Any?) {}
}