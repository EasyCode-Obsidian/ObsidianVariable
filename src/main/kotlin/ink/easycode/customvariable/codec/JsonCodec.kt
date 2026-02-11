package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object JsonCodec : TypeCodec {

    override val type: VariableType = VariableType.JSON

    override fun parseOrNull(raw: String): Any? {
        val text = raw.trim()
        if (text.isEmpty()) {
            return "{}"
        }
        val isObject = text.startsWith("{") && text.endsWith("}")
        val isArray = text.startsWith("[") && text.endsWith("]")
        return if (isObject || isArray) text else null
    }

    override fun format(value: Any): String {
        return value.toString().trim()
    }

    override fun defaultValue(): Any {
        return "{}"
    }
}
