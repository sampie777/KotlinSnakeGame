package gui.config

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.gui.config.formcomponents.*
import nl.sajansen.kotlinsnakegame.objects.game.HighScores
import java.awt.BorderLayout
import java.awt.GridLayout
import java.util.logging.Logger
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder
import kotlin.math.max
import kotlin.math.min

class ConfigEditPanel : JPanel() {
    private val logger = Logger.getLogger(ConfigEditPanel::class.java.name)

    private val formComponents: ArrayList<FormComponent> = ArrayList()

    init {
        createFormInputs()
        createGui()
    }

    private fun createFormInputs() {
        formComponents.add(HeaderFormComponent("General"))
        formComponents.add(
            NumberFormInput<Long>(
                "maxFps",
                "Maximum FPS",
                min = 1,
                max = null
            )
        )
        formComponents.add(
            NumberFormInput<Long>(
                "stepPerSeconds",
                "Game speed (updates/second)",
                min = 1,
                max = null
            )
        )

        formComponents.add(HeaderFormComponent("Game Play"))
        formComponents.add(BooleanFormInput("playerWarpsThroughWalls", "Warp through walls"))
        formComponents.add(BooleanFormInput("snakeEatsHumanPlayer", "Snake can eat Humans"))
        formComponents.add(BooleanFormInput("snakeCollidesWithWalls", "Collide with walls"))
        formComponents.add(
            BooleanFormInput(
                "snakeOnlyLeftRightControls",
                "Steer snake with only two directions (not four)"
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "snakeStepInterval",
                "Snake speed",
                min = 1,
                max = Int.MAX_VALUE
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "starMinSpawnTime",
                "Star minimum spawn time",
                min = 0,
                max = Int.MAX_VALUE,
                onSave = { max(0, min(it, Config.starMaxSpawnTime - 1)) }
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "starMaxSpawnTime",
                "Star maximum spawn time",
                min = 0,
                max = Int.MAX_VALUE,
                onSave = { max(it, Config.starMinSpawnTime + 1) }
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "starMinDieTimeout",
                "Star minimum time to live",
                min = 0,
                max = Int.MAX_VALUE,
                onSave = { max(0, min(it, Config.starMaxDieTimeout - 1)) }
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "starMaxDieTimeout",
                "Star maximum time to live",
                min = 0,
                max = Int.MAX_VALUE,
                onSave = { max(it, Config.starMinDieTimeout + 1) }
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "starEffectTime",
                "Star effect time",
                min = 0,
                max = Int.MAX_VALUE
            )
        )

        formComponents.add(
            BooleanFormInput(
                "",
                "Reset high scores",
                onSave = {
                    if (it) {
                        HighScores.clearHighScores()
                    }
                    false
                })
        )
    }

    private fun createGui() {
        layout = BorderLayout()

        val mainPanel = JPanel()
        mainPanel.layout = GridLayout(0, 1)
        mainPanel.border = EmptyBorder(10, 10, 10, 10)

        addConfigItems(mainPanel)

        val scrollPanelInnerPanel = JPanel(BorderLayout())
        scrollPanelInnerPanel.add(mainPanel, BorderLayout.PAGE_START)
        val scrollPanel = JScrollPane(scrollPanelInnerPanel)
        scrollPanel.border = null
        add(scrollPanel, BorderLayout.CENTER)
    }

    private fun addConfigItems(panel: JPanel) {
        formComponents.forEach {
            try {
                panel.add(it.component())
            } catch (e: Exception) {
                logger.severe("Failed to create Config Edit GUI component: ${it::class.java}")
                e.printStackTrace()

                if (it !is FormInput) {
                    return@forEach
                }

                logger.severe("Failed to create Config Edit GUI component: ${it.key}")
//                Notifications.add(
//                    "Failed to load GUI input for config key: <strong>${it.key}</strong>. Delete your <i>${PropertyLoader.getPropertiesFile().name}</i> file and try again.",
//                    "Configuration"
//                )
                panel.add(TextFormComponent("Failed to load component. See Notifications.").component())
            }
        }
    }

    fun saveAll(): Boolean {
        val formInputComponents = formComponents.filterIsInstance<FormInput>()
        val validationErrors = ArrayList<String>()

        formInputComponents.forEach {
            val validation = it.validate()
            if (validation.isEmpty()) {
                return@forEach
            }

            logger.warning(validation.toString())
            validationErrors += validation
        }

        if (validationErrors.isNotEmpty()) {
            if (this.parent == null) {
                logger.warning("Panel is not a visible GUI component")
                return false
            }

            JOptionPane.showMessageDialog(
                this, validationErrors.joinToString(",\n"),
                "Invalid data",
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }

        formInputComponents.forEach {
            if (it.key.isBlank()) {
                it.save()
                return@forEach
            }

            val oldValue = Config.get(it.key)
            it.save()
            val newValue = Config.get(it.key)

            if (oldValue != newValue) {
                EventHub.propertyUpdated(it.key, newValue)
            }
        }
        return true
    }
}
