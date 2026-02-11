package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object LongCodec : TypeCodec {

    override val type: VariableType = VariableType.LONG

    override fun parseOrNull(raw: String): Any? {
        return raw.trim().toLongOrNull()
    }

    override fun format(value: Any): String {
        return (value as Number).toLong().toString()
    }

    override fun defaultValue(): Any {
        return 0L
    }
}
