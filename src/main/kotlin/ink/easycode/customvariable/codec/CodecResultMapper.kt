package ink.easycode.customvariable.codec

import ink.easycode.customvariable.model.ServiceErrorCode
import ink.easycode.customvariable.model.ServiceResult
import ink.easycode.customvariable.model.VariableType

object CodecResultMapper {

    fun normalizeRequired(type: VariableType, raw: String): ServiceResult<String> {
        val normalized = DefaultTypeCodecRegistry.normalizeOrNull(type, raw)
            ?: return ServiceResult.Error(
                ServiceErrorCode.INVALID_TYPE_VALUE,
                arrayOf(type.name, raw)
            )

        return ServiceResult.Ok(normalized)
    }
}
