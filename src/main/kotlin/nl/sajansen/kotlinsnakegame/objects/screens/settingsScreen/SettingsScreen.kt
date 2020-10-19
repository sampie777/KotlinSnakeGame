package nl.sajansen.kotlinsnakegame.objects.screens.settingsScreen


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.game.Game
import nl.sajansen.kotlinsnakegame.objects.player.MovablePlayer
import nl.sajansen.kotlinsnakegame.objects.screens.Screen
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.*
import java.awt.Dimension
import java.awt.Font
import java.awt.Point
import java.util.logging.Logger

object SettingsScreen : Screen() {
    private val logger = Logger.getLogger(SettingsScreen::class.java.name)

    init {
        val titleLabel = Label("Settings")
        titleLabel.position = Point(0, 50)
        titleLabel.componentAlignmentX = ComponentAlignment.CENTER
        titleLabel.font = Font("Dialog", Font.BOLD, 32)
        components.add(titleLabel)

        val nameLabel = Label("Steer Snake using left/right only")
        nameLabel.position = Point(150, 150)
        components.add(nameLabel)

        val checkbox = CheckBox()
        checkbox.position = Point(600, 147)
        checkbox.isChecked = Config.snakeOnlyLeftRightControls
        checkbox.onChange = { value ->
            Config.snakeOnlyLeftRightControls = value
        }
        components.add(checkbox)

        val dummyButton = Button("Dummy button")
        dummyButton.position = Point(0, 300)
        dummyButton.componentAlignmentX = ComponentAlignment.CENTER
        dummyButton.onClick = { println("I'm clicked!") }
        components.add(dummyButton)

        val backButton = Button("Back")
        backButton.backgroundColor = null
        backButton.position = Point(10, 10)
        backButton.onClick = {
            close()
        }
        components.add(backButton)

        addKeyMappingButtons(Point(300, 400), Game.players.filterIsInstance<MovablePlayer>().first())
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