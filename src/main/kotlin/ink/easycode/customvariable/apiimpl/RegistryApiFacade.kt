package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiRegistryEntry
import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.api.ApiVariableScope
import ink.easycode.customvariable.api.ApiVariableType
import ink.easycode.customvariable.api.VariableRegistryApi
import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.service.RegistryService

object RegistryApiFacade : VariableRegistryApi {

    override fun register(
        scope: ApiVariableScope,
        key: String,
        type: ApiVariableType,
        defaultRaw: String?,
        description: String?
    ): ApiResult<ApiRegistryEntry> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = RegistryService.register(
            scope = ApiScopeMapper.toModel(scope),
            key = key,
            type = ApiTypeMapper.toModel(type),
            defaultRaw = defaultRaw,
            description = description
        )
        return ApiResultMapper.map(result, ApiModelMapper::mapRegistry)
    }

    override fun unregister(scope: ApiVariableScope, key: String): ApiResult<Unit> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = RegistryService.unregister(ApiScopeMapper.toModel(scope), key)
        return ApiResultMapper.mapUnit(result)
    }

    override fun changeType(
        scope: ApiVariableScope,
        key: String,
        newType: ApiVariableType
    ): ApiResult<ApiRegistryEntry> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val result = RegistryService.changeType(
            scope = ApiScopeMapper.toModel(scope),
            key = key,
            targetType = ApiTypeMapper.toModel(newType)
        )
        return ApiResultMapper.map(result, ApiModelMapper::mapRegistry)
    }

    override fun find(scope: ApiVariableScope, key: String): ApiResult<ApiRegistryEntry?> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val entry = CacheManager.getRegistry(ApiScopeMapper.toModel(scope), key)
        return ApiResult.Ok(entry?.let(ApiModelMapper::mapRegistry))
    }

    override fun list(scope: ApiVariableScope?): ApiResult<List<ApiRegistryEntry>> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        val entries = CacheManager.listRegistries(scope?.let(ApiScopeMapper::toModel))
        return ApiResult.Ok(entries.map(ApiModelMapper::mapRegistry))
    }
}
