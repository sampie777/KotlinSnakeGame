package nl.sajansen.kotlinsnakegame.objects

import java.awt.image.BufferedImage
import java.net.URL
import java.util.logging.Logger
import javax.imageio.ImageIO

enum class Sprites(val path: String, val frames: Int = 1) {
    UNKNOWN("nl/sajansen/kotlinsnakegame/sprites/unknown_1.png"),

    // Player
    PLAYER_FACE_1("nl/sajansen/kotlinsnakegame/sprites/player/player_face_1.png"),
    SNAKE_HEAD_1("nl/sajansen/kotlinsnakegame/sprites/player/snake_head_1.png"),
    SNAKE_BODY_1("nl/sajansen/kotlinsnakegame/sprites/player/snake_body_2.png"),

    GNOME_NEUTRAL_1("nl/sajansen/kotlinsnakegame/sprites/gnome/gnome_neutral_south_1.png"),
    GNOME_WALKING_NORTH_1("nl/sajansen/kotlinsnakegame/sprites/gnome/gnome_walking_north_1.png", frames = 3),
    GNOME_WALKING_EAST_1("nl/sajansen/kotlinsnakegame/sprites/gnome/gnome_walking_east_1.png", frames = 3),
    GNOME_WALKING_SOUTH_1("nl/sajansen/kotlinsnakegame/sprites/gnome/gnome_walking_south_1.png", frames = 3),
    GNOME_WALKING_WEST_1("nl/sajansen/kotlinsnakegame/sprites/gnome/gnome_walking_west_1.png", frames = 3),

    // Props
    BOX_1("nl/sajansen/kotlinsnakegame/sprites/props/box_1.png"),
    FOOD_1("nl/sajansen/kotlinsnakegame/sprites/props/food_1.png"),
    STAR_1("nl/sajansen/kotlinsnakegame/sprites/props/star_1.png", frames = 3),
    STAR_2("nl/sajansen/kotlinsnakegame/sprites/props/star_2.png", frames = 10);

    private val logger = Logger.getLogger(Sprites::class.java.name)

    var bufferedImage: BufferedImage? = null
        get() {
            if (field == null) {
                logger.info("Loading Sprite resource: $path")
                val spriteResource =
                    spriteResource(this) ?: throw IllegalArgumentException("Sprite resource not found: $path")
                field = ImageIO.read(spriteResource)
            }
            return field
        }

    companion object {
        private fun spriteResource(sprite: Sprites): URL? = this::class.java.classLoader.getResource(sprite.path)
    }
}