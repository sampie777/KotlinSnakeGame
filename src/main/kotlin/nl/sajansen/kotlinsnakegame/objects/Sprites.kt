package nl.sajansen.kotlinsnakegame.objects

enum class Sprites(val path: String, val frames: Int = 1) {
    UNKNOWN("nl/sajansen/kotlinsnakegame/sprites/unknown_1.png"),

    // Player
    PLAYER_FACE_1("nl/sajansen/kotlinsnakegame/sprites/player/player_face_1.png"),
    SNAKE_HEAD_1("nl/sajansen/kotlinsnakegame/sprites/player/snake_head_1.png"),
    SNAKE_BODY_1("nl/sajansen/kotlinsnakegame/sprites/player/snake_body_2.png"),

    // Props
    BOX_1("nl/sajansen/kotlinsnakegame/sprites/props/box_1.png"),
    FOOD_1("nl/sajansen/kotlinsnakegame/sprites/props/food_1.png"),
    STAR_1("nl/sajansen/kotlinsnakegame/sprites/props/star_1.png", frames = 3),
    STAR_2("nl/sajansen/kotlinsnakegame/sprites/props/star_2.png", frames = 10),
}