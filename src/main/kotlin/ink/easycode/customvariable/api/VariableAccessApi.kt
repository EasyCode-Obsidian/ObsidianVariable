package ink.easycode.customvariable.api

interface VariableAccessApi {

    fun setGlobal(key: String, rawValue: String): ApiResult<ApiVariableValue>

    fun getGlobal(key: String): ApiResult<String>

    fun deleteGlobal(key: String): ApiResult<Unit>

    fun setPlayer(playerUuid: String, key: String, rawValue: String): ApiResult<ApiVariableValue>

    fun getPlayer(playerUuid: String, key: String): ApiResult<String>

    fun deletePlayer(playerUuid: String, key: String): ApiResult<Unit>
}
