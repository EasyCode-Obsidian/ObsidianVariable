package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.VariableType
import java.math.BigDecimal

object DecimalCodec : TypeCodec {

    override val type: VariableType = VariableType.DECIMAL

    override fun parseOrNull(raw: String): Any? {
        return try {
            BigDecimal(raw.trim())
        } catch (_: NumberFormatException) {
            null
        }
    }

    override fun format(value: Any): String {
        val decimal = value as BigDecimal
        return decimal.stripTrailingZeros().toPlainString()
    }

    override fun defaultValue(): Any {
        return BigDecimal.ZERO
    }
}
