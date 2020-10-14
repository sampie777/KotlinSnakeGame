package nl.sajansen.kotlinsnakegame.gui.config.formcomponents

import nl.sajansen.kotlinsnakegame.config.Config
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.util.logging.Logger
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class NumberFormInput<T : Number>(
    override val key: String,
    private val labelText: String,
    private val min: T?,
    private val max: T?,
    private val onSave: ((newValue: T) -> T)? = null,
    private val onValidate: ((newValue: T) -> List<String>)? = null
) : FormInput {
    private val logger = Logger.getLogger(NumberFormInput::class.java.name)

    private val input = JSpinner()

    @Suppress("UNCHECKED_CAST")
    override fun component(): Component {
        val configValue: T? = Config.get(key) as? T

        val label = JLabel(labelText)
        label.font = Font(Config.fontFamily, Font.PLAIN, 12)

        input.model = SpinnerNumberModel(configValue, min as? Comparable<T>, max as? Comparable<T>, 1)
        input.preferredSize = Dimension(100, 20)

        val panel = JPanel()
        panel.layout = BorderLayout(10, 10)
        panel.add(label, BorderLayout.LINE_START)
        panel.add(input, BorderLayout.LINE_END)
        return panel
    }

    override fun validate(): List<String> {
        val errors = ArrayList<String>()

        if (min != null && value().toDouble() < min.toDouble()) {
            errors.add("Value for '$labelText' is to small")
        }
        if (max != null && value().toDouble() > max.toDouble()) {
            errors.add("Value for '$labelText' is to large")
        }

        if (onValidate != null) {
            errors.addAll(onValidate.invoke(value()))
        }

        return errors
    }

    override fun save() {
        val value = onSave?.invoke(value()) ?: value()

        Config.set(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun value(): T {
        return input.value as T
    }
}