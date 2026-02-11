package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

interface TypeCodecRegistry {

    fun parse(type: VariableType, raw: String): Any?

    fun format(type: VariableType, value: Any): String

    fun normalizeOrNull(type: VariableType, raw: String): String?

    fun defaultRaw(type: VariableType): String
}
