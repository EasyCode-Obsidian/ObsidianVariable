package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object ListStringCodec : TypeCodec {

    override val type: VariableType = VariableType.LIST_STRING

    override fun parseOrNull(raw: String): Any {
        if (raw.isBlank()) {
            return emptyList<String>()
        }
        return raw.split("|").map { it.trim() }
    }

    override fun format(value: Any): String {
        val list = value as List<*>
        return list.joinToString("|") { it?.toString().orEmpty() }
    }

    override fun defaultValue(): Any {
        return emptyList<String>()
    }
}
