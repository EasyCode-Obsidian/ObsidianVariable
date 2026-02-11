package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.api.ApiVariableValue
import ink.easycode.customvariable.api.VariableAccessApi
import ink.easycode.customvariable.service.ValueService

object AccessApiFacade : VariableAccessApi {

    override fun setGlobal(key: String, rawValue: String): ApiResult<ApiVariableValue> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = ValueService.setGlobal(key, rawValue)
        return ApiResultMapper.map(result, ApiModelMapper::mapValue)
    }

    override fun getGlobal(key: String): ApiResult<String> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return ApiResultMapper.map(ValueService.getGlobal(key)) { it }
    }

    override fun deleteGlobal(key: String): ApiResult<Unit> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return ApiResultMapper.mapUnit(ValueService.deleteGlobal(key))
    }

    override fun setPlayer(playerUuid: String, key: String, rawValue: String): ApiResult<ApiVariableValue> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = ValueService.setPlayer(playerUuid, key, rawValue)
        return ApiResultMapper.map(result, ApiModelMapper::mapValue)
    }

    override fun getPlayer(playerUuid: String, key: String): ApiResult<String> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return ApiResultMapper.map(ValueService.getPlayer(playerUuid, key)) { it }
    }

    override fun deletePlayer(playerUuid: String, key: String): ApiResult<Unit> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return ApiResultMapper.mapUnit(ValueService.deletePlayer(playerUuid, key))
    }
}
