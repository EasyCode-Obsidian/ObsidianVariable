package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.api.ApiVariableValue
import ink.easycode.customvariable.api.VariableQueryApi
import ink.easycode.customvariable.model.ServiceResult
import ink.easycode.customvariable.service.ValueService

object QueryApiFacade : VariableQueryApi {

    override fun listGlobalValues(): ApiResult<List<ApiVariableValue>> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = ValueService.listGlobal()
        return ApiResultMapper.map(result) { list ->
            list.map(ApiModelMapper::mapValue)
        }
    }

    override fun listPlayerValues(playerUuid: String): ApiResult<List<ApiVariableValue>> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = ValueService.listPlayer(playerUuid)
        return ApiResultMapper.map(result) { list ->
            list.map(ApiModelMapper::mapValue)
        }
    }

    override fun batchGetGlobal(keys: List<String>): ApiResult<Map<String, String>> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return batchGet(keys) { key -> ValueService.getGlobal(key) }
    }

    override fun batchGetPlayer(playerUuid: String, keys: List<String>): ApiResult<Map<String, String>> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return batchGet(keys) { key -> ValueService.getPlayer(playerUuid, key) }
    }

    private fun batchGet(
        keys: List<String>,
        supplier: (String) -> ServiceResult<String>
    ): ApiResult<Map<String, String>> {
        val result = linkedMapOf<String, String>()
        keys.forEach { key ->
            when (val value = supplier(key)) {
                is ServiceResult.Ok -> result[key] = value.value
                is ServiceResult.Error -> return ApiResultMapper.mapError(value)
            }
        }
        return ApiResult.Ok(result)
    }
}
