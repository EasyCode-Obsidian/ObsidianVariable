package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiErrorCode
import ink.easycode.customvariable.model.ServiceErrorCode

object ApiErrorMapper {

    fun map(code: ServiceErrorCode): ApiErrorCode {
        return when (code) {
            ServiceErrorCode.VARIABLE_NOT_REGISTERED -> ApiErrorCode.VARIABLE_NOT_REGISTERED
            ServiceErrorCode.VARIABLE_ALREADY_REGISTERED -> ApiErrorCode.VARIABLE_ALREADY_REGISTERED
            ServiceErrorCode.INVALID_KEY -> ApiErrorCode.INVALID_KEY
            ServiceErrorCode.INVALID_TYPE_VALUE -> ApiErrorCode.INVALID_TYPE_VALUE
            ServiceErrorCode.REGISTRY_DISABLED -> ApiErrorCode.REGISTRY_DISABLED
            ServiceErrorCode.PLAYER_NOT_FOUND -> ApiErrorCode.PLAYER_NOT_FOUND
            ServiceErrorCode.DATABASE_ERROR -> ApiErrorCode.DATABASE_ERROR
            ServiceErrorCode.INTERNAL_ERROR -> ApiErrorCode.INTERNAL_ERROR
        }
    }
}
