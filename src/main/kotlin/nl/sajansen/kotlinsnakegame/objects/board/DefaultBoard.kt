package nl.sajansen.kotlinsnakegame.objects.board


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.gui.utils.createGraphics
import nl.sajansen.kotlinsnakegame.objects.entities.CollidableEntity
import nl.sajansen.kotlinsnakegame.objects.entities.Entity
import nl.sajansen.kotlinsnakegame.objects.entities.Sprite
import nl.sajansen.kotlinsnakegame.objects.entities.props.Box
import nl.sajansen.kotlinsnakegame.objects.entities.props.Food
import nl.sajansen.kotlinsnakegame.objects.entities.props.Star
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.isEntityInEntity
import nl.sajansen.kotlinsnakegame.objects.isPointInSprite
import nl.sajansen.kotlinsnakegame.objects.isSpriteInSprite
import nl.sajansen.kotlinsnakegame.objects.lidar.Lidar
import nl.sajansen.kotlinsnakegame.objects.lidar.LidarEquipped
import nl.sajansen.kotlinsnakegame.objects.player.Player
import java.awt.*
import java.awt.image.BufferedImage
import java.util.logging.Logger
import kotlin.random.Random

class DefaultBoard : Board {
    private val logger = Logger.getLogger(DefaultBoard::class.java.name)

    override var gridSize = 32
    override var size = Dimension(28 * gridSize, 18 * gridSize)
    override var windowSize = Dimension(28 * gridSize, 18 * gridSize)
    override var windowPosition = Point(0, 0)
    override var entities = arrayListOf<Entity>()

    private var spawnStarAtTime = -1L

    init {
        loadBoard1()
    }

    private fun loadBoard1() {
        repeat(Random.nextInt(3, 9)) {
            val randomPoint = getRandomEmptyPoint(Box().size) ?: return@repeat
            entities.add(Box(randomPoint))
        }

        spawnRandomFood()
        spawnRandomStar()
    }

    private fun spriteEntities() = entities.toTypedArray().filterIsInstance<Sprite>()

    override fun reset() {
        entities.clear()

        loadBoard1()

        Game.players.toTypedArray().forEach {
            it.reset()
        }

        entities.toTypedArray()
            .filter { it !is Player }   // Player sprites already had a reset() call
            .forEach {
                it.reset()
            }
    }

    override fun step() {
        spawnStarIfNeeded()

        // Step all
        Game.players.toTypedArray().forEach { it.step() }
        entities.toTypedArray()
            .filter { it !is Player }   // Player sprites already had a step() call
            .forEach { it.step() }

        // Check for collisions
        checkCollisions(entities.toTypedArray())
    }

    private fun checkCollisions(entities: Array<Entity>) {
        entities.filterIsInstance<CollidableEntity>()
            .forEach { entity ->
                getEntitiesAt(entity as Sprite).forEach {
                    entity.collidedWith(it)
                }
            }
    }

    override fun paint(): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(size.width, size.height)

        val spriteEntitiesImage = paintSprites(spriteEntities())
        g.drawImage(spriteEntitiesImage, null, 0, 0)

        Lidar.scan(entities.filterIsInstance<LidarEquipped>(), image = spriteEntitiesImage)
        if (Config.displayLidarBeams) {
            g.drawImage(Lidar.beamsLayer, null, 0, 0)
        }

        if (Config.displayPlayerNames) {
            paintPlayerNames(g)
        }

        g.dispose()
        return bufferedImage.getSubimage(windowPosition.x, windowPosition.y, windowSize.width, windowSize.height)
    }

    private fun paintPlayerNames(g: Graphics2D) {
        Game.players.forEach {
            g.font = Font("Dialog", Font.PLAIN, 11)
            g.color = Color(189, 189, 189)
            it.paintName(g)
        }
    }

    private fun paintSprites(sprites: List<Sprite>): BufferedImage {
        val (bufferedImage, g: Graphics2D) = createGraphics(size.width, size.height)

        sprites.forEach {
            g.drawImage(it.paint(), null, it.position.x, it.position.y)
        }

        g.dispose()
        return bufferedImage
    }

    fun getEntitiesAt(entity: Entity): List<Entity> {
        return entities.filter { it != entity }
            .filter { isEntityInEntity(entity, it) }
    }

    fun getSpritesAt(sprite: Sprite): List<Sprite> {
        return spriteEntities().filter { it != sprite }
            .filter { isSpriteInSprite(sprite, it) }
    }

    fun getSpritesAt(position: Point): List<Sprite> {
        return spriteEntities().filter { isPointInSprite(position, it) }
    }

    fun getSpritesAt(position: Point, size: Dimension): List<Sprite> {
        return spriteEntities().filter { isPointInSprite(position, size, it) }
    }

    override fun spawnRandomFood() {
        logger.info("Time to spawn some food")
        val food = Food()

        val randomPoint = getRandomEmptyPoint(food.size)
        if (randomPoint == null) {
            logger.warning("Could not spawn new food: no empty random location found")
            Game.end("No room left for food")
            return
        }

        food.position = randomPoint
        entities.add(food)
    }

    private fun spawnStarIfNeeded() {
        if (spawnStarAtTime < 0 || spawnStarAtTime > Game.state.time) return

        logger.info("Time to spawn a star")
        Star.spawnAtRandomLocation()
        spawnStarAtTime = -1
    }

    override fun spawnRandomStar() {
        spawnStarAtTime =
            Game.state.time + Random.nextInt(Config.starMinSpawnTime, Config.starMaxSpawnTime) * Config.stepPerSeconds
        logger.info("Spawn next star at random time: $spawnStarAtTime")
    }

    override fun getRandomEmptyPoint(size: Dimension): Point? {
        val maxX = this.size.width / gridSize
        val maxY = this.size.height / gridSize

        var point = Point(0, 0)

        val maxTries = 2 * maxX * maxY
        for (currentTry in 0..maxTries) {
            point.x = Random.nextInt(0, maxX) * gridSize + (gridSize - size.width) / 2
            point.y = Random.nextInt(0, maxY) * gridSize + (gridSize - size.height) / 2

            if (getSpritesAt(point, size).isEmpty()) {
                return point
            }
        }

        logger.warning("Could not find a random empty spot. Trying to find non random empty spot")

        // Scan all possible points
        point = Point(0, 0)
        (0 until maxX).forEach { x ->
            (0 until maxY).forEach { y ->
                point.x = x
                point.y = y

                if (getSpritesAt(point, size).isEmpty()) {
                    return point
                }
            }
        }

        logger.warning("No empty spot found on board")
        return null
    }
}