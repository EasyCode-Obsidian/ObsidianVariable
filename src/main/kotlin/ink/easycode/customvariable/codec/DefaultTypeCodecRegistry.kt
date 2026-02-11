package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType

object DefaultTypeCodecRegistry : TypeCodecRegistry {

    private val codecs: Map<VariableType, TypeCodec> = listOf(
        StringCodec,
        IntCodec,
        LongCodec,
        DoubleCodec,
        DecimalCodec,
        BooleanCodec,
        UuidCodec,
        TextCodec,
        JsonCodec,
        ListStringCodec
    ).associateBy { it.type }

    override fun parse(type: VariableType, raw: String): Any? {
        return codec(type).parseOrNull(raw)
    }

    override fun format(type: VariableType, value: Any): String {
        return codec(type).format(value)
    }

    override fun normalizeOrNull(type: VariableType, raw: String): String? {
        return codec(type).normalizeOrNull(raw)
    }

    override fun defaultRaw(type: VariableType): String {
        return codec(type).format(codec(type).defaultValue())
    }

    private fun codec(type: VariableType): TypeCodec {
        return codecs[type] ?: error("Missing codec for type: $type")
    }
}
