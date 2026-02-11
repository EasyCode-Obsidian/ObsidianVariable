package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object BooleanCodec : TypeCodec {

    private val trueValues = setOf("true", "1", "yes", "y", "on")
    private val falseValues = setOf("false", "0", "no", "n", "off")

    override val type: VariableType = VariableType.BOOLEAN

    override fun parseOrNull(raw: String): Any? {
        return when (raw.trim().lowercase()) {
            in trueValues -> true
            in falseValues -> false
            else -> null
        }
    }

    override fun format(value: Any): String {
        return (value as Boolean).toString()
    }

    override fun defaultValue(): Any {
        return false
    }
}
