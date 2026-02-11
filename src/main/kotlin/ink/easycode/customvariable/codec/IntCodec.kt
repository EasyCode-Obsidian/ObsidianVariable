package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object IntCodec : TypeCodec {

    override val type: VariableType = VariableType.INT

    override fun parseOrNull(raw: String): Any? {
        return raw.trim().toIntOrNull()
    }

    override fun format(value: Any): String {
        return (value as Number).toInt().toString()
    }

    override fun defaultValue(): Any {
        return 0
    }
}
