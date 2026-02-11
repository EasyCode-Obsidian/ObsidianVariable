package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object DoubleCodec : TypeCodec {

    override val type: VariableType = VariableType.DOUBLE

    override fun parseOrNull(raw: String): Any? {
        return raw.trim().toDoubleOrNull()
    }

    override fun format(value: Any): String {
        return (value as Number).toDouble().toString()
    }

    override fun defaultValue(): Any {
        return 0.0
    }
}
