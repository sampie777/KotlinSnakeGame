package nl.sajansen.kotlinsnakegame.objects.sound

enum class Sounds(val path: String, val defaultVolume: Float = 1.0f) {
    GAME_END("nl/sajansen/kotlinsnakegame/audio/game-over.wav", 0.85f),
    EAT_FOOD("nl/sajansen/kotlinsnakegame/audio/snake-eat-food.wav"),
    SNAKE_EAT_GNOME("nl/sajansen/kotlinsnakegame/audio/snake-eat-gnome.wav", 0.9f),
    STEP_1("nl/sajansen/kotlinsnakegame/audio/steps-1.wav", 0.5f),
    STEP_2("nl/sajansen/kotlinsnakegame/audio/steps-2.wav", 0.5f),
}