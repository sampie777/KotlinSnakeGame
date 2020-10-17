package nl.sajansen.kotlinsnakegame.objects.screens.settingsScreen


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.objects.screens.Screen
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Button
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.CheckBox
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.ComponentAlignment
import nl.sajansen.kotlinsnakegame.objects.screens.drawableComponents.Label
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
    }
}