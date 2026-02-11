package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType
import java.util.UUID

object UuidCodec : TypeCodec {

    override val type: VariableType = VariableType.UUID

    override fun parseOrNull(raw: String): Any? {
        return try {
            UUID.fromString(raw.trim())
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override fun format(value: Any): String {
        return (value as UUID).toString()
    }

    override fun defaultValue(): Any {
        return UUID.fromString("00000000-0000-0000-0000-000000000000")
    }
}
