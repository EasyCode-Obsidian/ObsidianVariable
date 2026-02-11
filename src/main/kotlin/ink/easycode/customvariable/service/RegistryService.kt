package ink.easycode.customvariable.service

import ink.easycode.customvariable.apiimpl.event.ApiEventPublisher
import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.ServiceErrorCode
import ink.easycode.customvariable.model.ServiceResult
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableType
import ink.easycode.customvariable.repository.RepositoryManager
import ink.easycode.customvariable.sync.SyncManager
import ink.easycode.customvariable.util.VariableKeyValidator

object RegistryService {

    fun register(
        scope: VariableScope,
        key: String,
        type: VariableType,
        defaultRaw: String?,
        description: String?,
        enabled: Boolean = true
    ): ServiceResult<RegistryEntry> {
        return ServiceErrorMapper.map {
            if (!VariableKeyValidator.isValid(key)) {
                return@map ServiceResult.Error(ServiceErrorCode.INVALID_KEY, arrayOf(key))
            }

            val exists = CacheManager.getRegistry(scope, key)
            if (exists != null) {
                return@map ServiceResult.Error(
                    ServiceErrorCode.VARIABLE_ALREADY_REGISTERED,
                    arrayOf(scope.name, key)
                )
            }

            val normalizedDefault = RegistryDefaultResolver.normalizeForRegister(type, defaultRaw)
            if (defaultRaw != null && normalizedDefault == null) {
                return@map ServiceResult.Error(
                    ServiceErrorCode.INVALID_TYPE_VALUE,
                    arrayOf(type.name, defaultRaw)
                )
            }

            val now = System.currentTimeMillis()
            val entry = RegistryEntry(
                scope = scope,
                key = key,
                type = type,
                defaultRaw = normalizedDefault,
                description = description?.trim()?.ifEmpty { null },
                enabled = enabled,
                version = 1L,
                updatedAt = now
            )
            SyncManager.enqueueRegistryUpsert(entry)
            RegistryValueSeeder.seedOnRegister(entry)
            ApiEventPublisher.publishRegistered(entry)
            ServiceResult.Ok(entry)
        }
    }

    fun unregister(scope: VariableScope, key: String): ServiceResult<Boolean> {
        return ServiceErrorMapper.map {
            if (!VariableKeyValidator.isValid(key)) {
                return@map ServiceResult.Error(ServiceErrorCode.INVALID_KEY, arrayOf(key))
            }
            val exists = CacheManager.getRegistry(scope, key)
                ?: return@map ServiceResult.Error(
                    ServiceErrorCode.VARIABLE_NOT_REGISTERED,
                    arrayOf(scope.name, key)
                )

            SyncManager.enqueueRegistryDelete(scope, key)
            ApiEventPublisher.publishDeleted(scope, "", key)
            ServiceResult.Ok(exists.enabled)
        }
    }

    fun changeType(scope: VariableScope, key: String, targetType: VariableType): ServiceResult<RegistryEntry> {
        return ServiceErrorMapper.map {
            if (!VariableKeyValidator.isValid(key)) {
                return@map ServiceResult.Error(ServiceErrorCode.INVALID_KEY, arrayOf(key))
            }

            val current = CacheManager.getRegistry(scope, key)
                ?: return@map ServiceResult.Error(
                    ServiceErrorCode.VARIABLE_NOT_REGISTERED,
                    arrayOf(scope.name, key)
                )

            val oldType = current.type
            val resetRaw = RegistryDefaultResolver.resolveForTypeChange(current.defaultRaw, targetType)
            val now = System.currentTimeMillis()
            val updated = current.copy(
                type = targetType,
                defaultRaw = resetRaw,
                version = current.version + 1,
                updatedAt = now
            )

            SyncManager.enqueueRegistryUpsert(updated)
            RepositoryManager.value().resetAllByKey(scope, key, resetRaw)
            CacheManager.invalidateByKey(key)
            if (scope == VariableScope.GLOBAL) {
                ValueService.refreshGlobalDefault(key, resetRaw, now)
            }
            ApiEventPublisher.publishTypeChanged(scope, key, oldType, targetType, resetRaw)
            ServiceResult.Ok(updated)
        }
    }
}
