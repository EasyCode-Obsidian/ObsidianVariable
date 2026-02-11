package ink.easycode.customvariable.api

class ApiException(
    val code: ApiErrorCode,
    val detail: String? = null,
    val args: List<String> = emptyList()
) : RuntimeException(detail ?: code.name)
