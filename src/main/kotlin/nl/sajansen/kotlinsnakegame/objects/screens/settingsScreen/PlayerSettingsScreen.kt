package nl.sajansen.kotlinsnakegame.objects.screens.settingsScreen


import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.getAvailableColorForSnake
import nl.sajansen.kotlinsnakegame.objects.player.HumanPlayer
import nl.sajansen.kotlinsnakegame.objects.player.MovablePlayer
import nl.sajansen.kotlinsnakegame.objects.player.Player
import nl.sajansen.kotlinsnakegame.objects.player.SnakePlayer
import nl.sajansen.kotlinsnakegame.objects.screens.*
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.*
import java.awt.Color
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
        add(titleLabel)

        val backButton = backButton()
        backButton.onClick = {
            close()
            SettingsScreen.rebuildGui()
        }
        add(backButton)

        addNameTextField(Point(150, 150))

        addSwitchTypeButton(Point(150, 200))

        if (player is SnakePlayer) {
            addColorChooserButton(Point(150, 250))
        }

        if (player is MovablePlayer) {
            addKeyMappingButtons(Point(250, 350), player)
        }
    }

    private fun addNameTextField(position: Point) {
        val label = Label("Name")
        label.position = position
        add(label)

        val textField = TextField(player.name)
        textField.position = Point(position.x + 300, position.y)
        textField.size = Dimension(260, 0)
        textField.maxLength = 12
        textField.onChange = { player.name = it }
        add(textField)
    }

    private fun addColorChooserButton(position: Point) {
        if (player !is SnakePlayer) {
            return
        }

        val label = Label("Color")
        label.position = position
        add(label)

        val button = Button()
        button.backgroundColor = player.color
        button.position = Point(position.x + 300, position.y)
        button.onClick = {
            player.color = getNextSnakePlayerColor(player)
            button.backgroundColor = player.color
        }
        add(button)
    }

    private fun getNextSnakePlayerColor(player: SnakePlayer): Color {
        val currentColorIndex = SnakePlayer.availableColors.indexOf(player.color)
        if (currentColorIndex + 1 >= SnakePlayer.availableColors.size) {
            return SnakePlayer.availableColors[0]
        }
        return SnakePlayer.availableColors[currentColorIndex + 1]
    }

    private fun addSwitchTypeButton(position: Point) {
        val label = Label("Type")
        label.position = position
        add(label)

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
        add(button)
    }

    private fun switchPlayerToSnakePlayer() {
        val newPlayer = player.changeTo(SnakePlayer::class.java)
        getAvailableColorForSnake(newPlayer)
        replacePlayer(player, newPlayer)
    }

    private fun switchPlayerToHumanPlayer() {
        val newPlayer = player.changeTo(HumanPlayer::class.java)
        replacePlayer(player, newPlayer)
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
        add(upKeyButton)

        val leftKeyButton = KeyMappingButton(player.leftKey)
        leftKeyButton.position = Point(position.x + 0, position.y + verticalSpacing)
        leftKeyButton.size = size
        leftKeyButton.allowEmpty = false
        leftKeyButton.onSave = { player.leftKey = it!!.keyCode }
        add(leftKeyButton)

        val downKeyButton = KeyMappingButton(player.downKey)
        downKeyButton.position = Point(position.x + horizontalSpacing, position.y + verticalSpacing)
        downKeyButton.size = size
        downKeyButton.allowEmpty = false
        downKeyButton.onSave = { player.downKey = it!!.keyCode }
        add(downKeyButton)

        val rightKeyButton = KeyMappingButton(player.rightKey)
        rightKeyButton.position = Point(position.x + 2 * horizontalSpacing, position.y + verticalSpacing)
        rightKeyButton.size = size
        rightKeyButton.allowEmpty = false
        rightKeyButton.onSave = { player.rightKey = it!!.keyCode }
        add(rightKeyButton)
    }
}