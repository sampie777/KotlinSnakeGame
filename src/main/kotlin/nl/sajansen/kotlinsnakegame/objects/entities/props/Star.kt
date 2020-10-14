package nl.sajansen.kotlinsnakegame.objects.entities.props


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.Sprite
import nl.sajansen.kotlinsnakegame.objects.Sprites
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Dimension
import java.awt.Point
import java.util.logging.Logger
import kotlin.random.Random

class Star(
    override var position: Point = Point(0, 0),
    override var solid: Boolean = false
) : Sprite() {
    private val logger = Logger.getLogger(Star::class.java.name)

    companion object {
        private val logger = Logger.getLogger(Star::class.java.name)

        fun spawnAtRandomLocation() {
            val star = Star()

            val randomPoint = Game.board.getRandomEmptyPoint(star.size)
            if (randomPoint == null) {
                logger.warning("Could not spawn new Star: no empty random location found")
                Game.end("No room left for Star")
                return
            }

            star.position = randomPoint
            Game.board.entities.add(star)
            star.ignite()
        }
    }

    override var sprite = Sprites.STAR_2
    override var size = Dimension(32, 30)

    private var dieAtTime = -1L

    override fun reset() {
    }

    override fun step() {
        if (dieAtTime > 0 && dieAtTime < Game.state.time) {
            die()
        }
    }

    fun ignite() {
        logger.info("Igniting star")
        dieAtTime = Game.state.time + Random.nextInt(Config.starMinDieTimeout, Config.starMaxDieTimeout) * Config.stepPerSeconds
        logger.info("Star self destruction time set to: $dieAtTime")
    }

    fun die() {
        logger.info("Star self destruction")
        destroy()
        Game.board.spawnRandomStar()
    }
}