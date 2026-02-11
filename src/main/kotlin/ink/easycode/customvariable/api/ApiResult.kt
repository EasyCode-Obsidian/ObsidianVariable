package ink.easycode.customvariable.api

sealed interface ApiResult<out T> {

    data class Ok<T>(val data: T) : ApiResult<T>

    data class Error(
        val code: ApiErrorCode,
        val message: String? = null,
        val args: List<String> = emptyList()
    ) : ApiResult<Nothing>
}
