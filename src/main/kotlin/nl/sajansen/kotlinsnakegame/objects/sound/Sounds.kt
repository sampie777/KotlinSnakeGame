package nl.sajansen.kotlinsnakegame.objects.sound

enum class Sounds(val path: String, val defaultVolume: Float = 1.0f) {
    GAME_END("nl/sajansen/kotlinsnakegame/audio/game-over.wav", 0.85f),
    EAT_FOOD("nl/sajansen/kotlinsnakegame/audio/snake-eat-food.wav"),
    SNAKE_EAT_GNOME("nl/sajansen/kotlinsnakegame/audio/snake-eat-gnome.wav", 0.9f),
}