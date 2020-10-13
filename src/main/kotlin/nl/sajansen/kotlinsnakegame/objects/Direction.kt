package nl.sajansen.kotlinsnakegame.objects

enum class Direction(val value: Int) {
    NONE(-1),
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    companion object {
        fun fromValue(value: Int) = values().firstOrNull { it.value == value }
    }
}