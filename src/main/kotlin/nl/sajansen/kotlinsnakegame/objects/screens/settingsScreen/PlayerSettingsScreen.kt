package nl.sajansen.kotlinsnakegame.objects.screens.settingsScreen


import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.player.HumanPlayer
import nl.sajansen.kotlinsnakegame.objects.player.MovablePlayer
import nl.sajansen.kotlinsnakegame.objects.player.Player
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import nl.sajansen.kotlinsnakegame.objects.screens.GameOverlay
import nl.sajansen.kotlinsnakegame.objects.screens.PauseScreen
import nl.sajansen.kotlinsnakegame.objects.screens.Screen
import nl.sajansen.kotlinsnakegame.objects.screens.StartScreen
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Button
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.KeyMappingButton
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
import java.awt.Dimension
import java.awt.Font
import java.awt.Point
import java.util.logging.Logger

class PlayerSettingsScreen(private val player: Player) : Screen() {
    private val logger = Logger.getLogger(PlayerSettingsScreen::class.java.name)

    init {
        initGui()
    }

    private fun initGui() {
        val titleLabel = Label(player.name)
        titleLabel.position = Point(0, 50)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        titleLabel.font = Font("Dialog", Font.BOLD, 32)
        components.add(titleLabel)

        val backButton = Button("Back")
        backButton.backgroundColor = null
        backButton.position = Point(10, 10)
        backButton.onClick = {
            close()
        }
        components.add(backButton)

        addSwitchTypeButton(Point(100, 150))

        if (player !is MovablePlayer) return

        addKeyMappingButtons(Point(250, 250), player)
    }

    private fun addSwitchTypeButton(position: Point) {
        val label = Label("Player type")
        label.position = position
        components.add(label)

        val button = Button(player::class.java.simpleName)
        button.backgroundColor = null
        button.position = Point(position.x + 300, position.y)
        button.onClick = {
            if (player is HumanPlayer) {
                switchPlayerToSnakePlayer()
            } else {
                switchPlayerToHumanPlayer()
            }

            button.text = player::class.java.simpleName
        }
        components.add(button)
    }

    private fun switchPlayerToSnakePlayer() {
        val newPlayer = SnakePlayer()
        copyPropertiesToNewPlayer(player, newPlayer)
        replacePlayer(player, newPlayer)
    }

    private fun switchPlayerToHumanPlayer() {
        val newPlayer = HumanPlayer()
        copyPropertiesToNewPlayer(player, newPlayer)
        replacePlayer(player, newPlayer)
    }

    private fun copyPropertiesToNewPlayer(oldPlayer: Player, newPlayer: Player) {
        newPlayer.name = oldPlayer.name

        if (oldPlayer is MovablePlayer && newPlayer is MovablePlayer) {
            newPlayer.upKey = oldPlayer.upKey
            newPlayer.rightKey = oldPlayer.rightKey
            newPlayer.downKey = oldPlayer.downKey
            newPlayer.leftKey = oldPlayer.leftKey
        }
    }

    private fun replacePlayer(oldPlayer: Player, newPlayer: Player) {
        Game.remove(oldPlayer)
        Game.addPlayer(newPlayer)

        close()
        PlayerSettingsScreen(newPlayer).show()
        requireGameRestart()
    }

    private fun requireGameRestart() {
        GameOverlay.close()
        PauseScreen.close()
        StartScreen.show(0)
    }

    private fun addKeyMappingButtons(position: Point, player: MovablePlayer) {
        val horizontalSpacing = 135
        val verticalSpacing = 55
        val size = Dimension(130, 50)

        val upKeyButton = KeyMappingButton(player.upKey)
        upKeyButton.position = Point(position.x + horizontalSpacing, position.y + 0)
        upKeyButton.size = size
        upKeyButton.allowEmpty = false
        upKeyButton.onSave = { player.upKey = it!!.keyCode }
        components.add(upKeyButton)

        val leftKeyButton = KeyMappingButton(player.leftKey)
        leftKeyButton.position = Point(position.x + 0, position.y + verticalSpacing)
        leftKeyButton.size = size
        leftKeyButton.allowEmpty = false
        leftKeyButton.onSave = { player.leftKey = it!!.keyCode }
        components.add(leftKeyButton)

        val downKeyButton = KeyMappingButton(player.downKey)
        downKeyButton.position = Point(position.x + horizontalSpacing, position.y + verticalSpacing)
        downKeyButton.size = size
        downKeyButton.allowEmpty = false
        downKeyButton.onSave = { player.downKey = it!!.keyCode }
        components.add(downKeyButton)

        val rightKeyButton = KeyMappingButton(player.rightKey)
        rightKeyButton.position = Point(position.x + 2 * horizontalSpacing, position.y + verticalSpacing)
        rightKeyButton.size = size
        rightKeyButton.allowEmpty = false
        rightKeyButton.onSave = { player.rightKey = it!!.keyCode }
        components.add(rightKeyButton)
    }
}