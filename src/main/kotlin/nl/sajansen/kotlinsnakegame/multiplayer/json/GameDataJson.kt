package nl.sajansen.kotlinsnakegame.multiplayer.json


data class GameDataJson(
    var isEnded: Boolean,
    var players: List<PlayerDataJson>,
)