package nl.sajansen.kotlinsnakegame.multiplayer.json

import nl.sajansen.kotlinsnakegame.multiplayer.Commands


data class JsonMessage(
    val command: Commands? = null,
    val message: String? = null,
    val obj: Any? = null,
    val objClassName: String? = null
)