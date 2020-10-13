package gui.config

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.gui.config.formcomponents.*
import java.awt.BorderLayout
import java.awt.GridLayout
import java.util.logging.Logger
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

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
                "paintFPS",
                "FPS",
                min = 1,
                max = null
            )
        )
        formComponents.add(
            NumberFormInput<Long>(
                "stepInterval",
                "Game speed",
                min = 1,
                max = null
            )
        )

        formComponents.add(HeaderFormComponent("Game Play"))
        formComponents.add(BooleanFormInput("playerWarpsThroughWalls", "Warp through walls"))
        formComponents.add(BooleanFormInput("snakeCollidesWithWalls", "Collide with walls"))
        formComponents.add(
            NumberFormInput<Int>(
                "snakeStepInterval",
                "Snake speed",
                min = 1,
                max = Int.MAX_VALUE
            )
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
