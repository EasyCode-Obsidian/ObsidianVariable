package ink.easycode.customvariable.api

interface VariableQueryApi {

    fun listGlobalValues(): ApiResult<List<ApiVariableValue>>

    fun listPlayerValues(playerUuid: String): ApiResult<List<ApiVariableValue>>

    fun batchGetGlobal(keys: List<String>): ApiResult<Map<String, String>>

    fun batchGetPlayer(playerUuid: String, keys: List<String>): ApiResult<Map<String, String>>
}
