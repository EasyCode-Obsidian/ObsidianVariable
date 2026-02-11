package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

interface TypeCodec {

    val type: VariableType

    fun parseOrNull(raw: String): Any?

    fun format(value: Any): String

    fun defaultValue(): Any

    fun normalizeOrNull(raw: String): String? {
        val parsed = parseOrNull(raw) ?: return null
        return format(parsed)
    }
}
