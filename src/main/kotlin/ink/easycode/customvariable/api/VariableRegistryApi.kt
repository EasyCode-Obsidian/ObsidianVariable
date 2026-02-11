package ink.easycode.customvariable.api

interface VariableRegistryApi {

    fun register(
        scope: ApiVariableScope,
        key: String,
        type: ApiVariableType,
        defaultRaw: String? = null,
        description: String? = null
    ): ApiResult<ApiRegistryEntry>

    fun unregister(scope: ApiVariableScope, key: String): ApiResult<Unit>

    fun changeType(
        scope: ApiVariableScope,
        key: String,
        newType: ApiVariableType
    ): ApiResult<ApiRegistryEntry>

    fun find(scope: ApiVariableScope, key: String): ApiResult<ApiRegistryEntry?>

    fun list(scope: ApiVariableScope? = null): ApiResult<List<ApiRegistryEntry>>
}
