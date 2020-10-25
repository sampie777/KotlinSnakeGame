package nl.sajansen.kotlinsnakegame.objects.screens.settingsScreen


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.player.HumanPlayer
import nl.sajansen.kotlinsnakegame.objects.player.Player
import nl.sajansen.kotlinsnakegame.objects.screens.Screen
import nl.sajansen.kotlinsnakegame.objects.screens.backButton
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Button
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.CheckBox
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Point
import java.util.logging.Logger

object SettingsScreen : Screen() {
    private val logger = Logger.getLogger(SettingsScreen::class.java.name)

    private val addPlayerButton = Button("Add player")

    init {
        rebuildGui()
    }

    override fun show(index: Int?) {
        super.show(index)
        rebuildGui()
    }

    fun rebuildGui() {
        components.clear()
        initGui()
    }

    private fun initGui() {
        val titleLabel = Label("Settings")
        titleLabel.position = Point(0, 50)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        titleLabel.font = Font("Dialog", Font.BOLD, 32)
        add(titleLabel)

        val nameLabel = Label("Steer Snake using left/right only")
        nameLabel.position = Point(150, 150)
        add(nameLabel)

        val checkbox = CheckBox()
        checkbox.position = Point(600, 147)
        checkbox.isChecked = Config.snakeOnlyLeftRightControls
        checkbox.onChange = { value ->
            Config.snakeOnlyLeftRightControls = value
        }
        add(checkbox)

        add(backButton())

        addPlayerSettingsRows(Point(150, 220))
    }

    private fun addPlayerSettingsRows(position: Point) {
        val rowVerticalMargin = 50

        Game.players.sortedBy { it.name }
            .forEachIndexed { index, player ->
            playerSettingsRow(
                Point(position.x, position.y + index * rowVerticalMargin),
                player
            )
        }

        checkForGamePlayerCapacityForAddPlayerButton()
        addPlayerButton.position = Point(position.x, position.y + Game.players.size * rowVerticalMargin)
        addPlayerButton.onClick = { addPlayer() }
        add(addPlayerButton)
    }

    private fun playerSettingsRow(position: Point, player: Player) {
        val nameLabel = Label(player.name)
        nameLabel.position = position
        nameLabel.font = Font("Dialog", Font.ITALIC, 20)
        add(nameLabel)

        val playerSettingsButton = Button("Settings")
        playerSettingsButton.position = Point(position.x + 300, position.y)
        playerSettingsButton.onClick = { showSettingsForPlayer(player) }
        add(playerSettingsButton)

        val playerRemoveButton = Button("X")
        playerRemoveButton.position = Point(position.x + 500, position.y)
        playerRemoveButton.margin = Dimension(10, 10)
        playerRemoveButton.backgroundColor = Color(168, 66, 66)
        playerRemoveButton.onClick = { removePlayer(player) }
        add(playerRemoveButton)
    }

    private fun showSettingsForPlayer(player: Player) {
        logger.info("showSettingsForPlayer clicked for $player")
        PlayerSettingsScreen(player).show()
    }

    private fun removePlayer(player: Player) {
        logger.info("RemovePlayer clicked for $player")
        Game.remove(player)
        rebuildGui()
    }

    private fun addPlayer() {
        if (Game.players.size >= Config.maxPlayers) {
            logger.info("Cannot add player. Max players reached")
            addPlayerButton.isVisible = false
            return
        }

        Game.addPlayer(HumanPlayer())
        rebuildGui()
    }

    private fun checkForGamePlayerCapacityForAddPlayerButton() {
        if (Game.players.size < Config.maxPlayers) {
            addPlayerButton.isVisible = true
            return
        }

        logger.info("Cannot add player. Max players reached")
        addPlayerButton.isVisible = false
    }
}