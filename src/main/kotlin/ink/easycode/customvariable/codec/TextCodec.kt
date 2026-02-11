package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object TextCodec : TypeCodec {

    override val type: VariableType = VariableType.TEXT

    override fun parseOrNull(raw: String): Any {
        return raw
    }

    override fun format(value: Any): String {
        return value.toString()
    }

    override fun defaultValue(): Any {
        return ""
    }
}
