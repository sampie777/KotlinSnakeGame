package nl.sajansen.kotlinsnakegame.objects.sound

enum class Sounds(val path: String) {
    GAME_END("nl/sajansen/kotlinsnakegame/audio/game-over.wav"),
    EAT_FOOD("nl/sajansen/kotlinsnakegame/audio/snake-eat-food.wav"),
    SNAKE_EAT_GNOME("nl/sajansen/kotlinsnakegame/audio/snake-eat-gnome.wav"),
}