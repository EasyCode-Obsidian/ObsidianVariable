package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object StringCodec : TypeCodec {

    override val type: VariableType = VariableType.STRING

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
