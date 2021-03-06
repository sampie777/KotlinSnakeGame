package nl.sajansen.kotlinsnakegame.gui.config.formcomponents

import nl.sajansen.kotlinsnakegame.config.Config
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.util.logging.Logger
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class SelectFormInput<T : Any>(
    override val key: String,
    private val labelText: String,
    private val values: List<T>
) : FormInput {

    private val logger = Logger.getLogger(SelectFormInput::class.java.name)

    private val selectBox = JComboBox<Any>()

    @Suppress("UNCHECKED_CAST")
    override fun component(): Component {
        val label = JLabel(labelText)
        label.font = Font(Config.fontFamily, Font.PLAIN, 12)

        val valuesArray = (values.toList() as ArrayList).toArray()
        selectBox.model = DefaultComboBoxModel(valuesArray)
        selectBox.selectedItem = values.find { it == Config.get(key) }
        selectBox.preferredSize = Dimension(100, 30)

        val panel = JPanel()
        panel.layout = BorderLayout(10, 10)
        panel.add(label, BorderLayout.LINE_START)
        panel.add(selectBox, BorderLayout.LINE_END)
        return panel
    }

    override fun validate(): List<String> {
        val errors = ArrayList<String>()

        if (!values.contains(value())) {
            errors.add("Selected invalid value '${value()}' for '$labelText'")
        }

        return errors
    }

    override fun save() {
        Config.set(key, value().toString())
    }

    @Suppress("UNCHECKED_CAST")
    override fun value(): T {
        return selectBox.selectedItem as T
    }
}