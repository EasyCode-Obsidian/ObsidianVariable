package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.model.ServiceResult

object ApiResultMapper {

    fun <T, R> map(result: ServiceResult<T>, transform: (T) -> R): ApiResult<R> {
        return when (result) {
            is ServiceResult.Ok -> ApiResult.Ok(transform(result.value))
            is ServiceResult.Error -> mapError(result)
        }
    }

    fun <T> mapUnit(result: ServiceResult<T>): ApiResult<Unit> {
        return map(result) { Unit }
    }

    fun <T> mapError(error: ServiceResult.Error): ApiResult<T> {
        return ApiResult.Error(
            code = ApiErrorMapper.map(error.code),
            args = error.args.map { it.toString() }
        )
    }
}
