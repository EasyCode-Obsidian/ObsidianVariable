package ink.easycode.customvariable.service

import ink.easycode.customvariable.apiimpl.event.ApiEventPublisher
import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.codec.DefaultTypeCodecRegistry
import ink.easycode.customvariable.model.ServiceErrorCode
import ink.easycode.customvariable.model.ServiceResult
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import ink.easycode.customvariable.sync.SyncManager
import ink.easycode.customvariable.util.OwnerIdResolver

object ValueService {

    fun setGlobal(key: String, raw: String): ServiceResult<VariableValue> {
        return setValue(VariableScope.GLOBAL, OwnerIdResolver.globalOwnerId(), key, raw)
    }

    fun setPlayer(ownerId: String, key: String, raw: String): ServiceResult<VariableValue> {
        val normalizedOwner = ownerId.trim()
        if (normalizedOwner.isEmpty()) {
            return ServiceResult.Error(ServiceErrorCode.PLAYER_NOT_FOUND, arrayOf(ownerId))
        }
        return setValue(VariableScope.PLAYER, normalizedOwner, key, raw)
    }

    fun getGlobal(key: String): ServiceResult<String> {
        return getValue(VariableScope.GLOBAL, OwnerIdResolver.globalOwnerId(), key)
    }

    fun getPlayer(ownerId: String, key: String): ServiceResult<String> {
        val normalizedOwner = ownerId.trim()
        if (normalizedOwner.isEmpty()) {
            return ServiceResult.Error(ServiceErrorCode.PLAYER_NOT_FOUND, arrayOf(ownerId))
        }
        return getValue(VariableScope.PLAYER, normalizedOwner, key)
    }

    fun deleteGlobal(key: String): ServiceResult<Boolean> {
        return deleteValue(VariableScope.GLOBAL, OwnerIdResolver.globalOwnerId(), key)
    }

    fun deletePlayer(ownerId: String, key: String): ServiceResult<Boolean> {
        val normalizedOwner = ownerId.trim()
        if (normalizedOwner.isEmpty()) {
            return ServiceResult.Error(ServiceErrorCode.PLAYER_NOT_FOUND, arrayOf(ownerId))
        }
        return deleteValue(VariableScope.PLAYER, normalizedOwner, key)
    }

    fun listGlobal(): ServiceResult<List<VariableValue>> {
        return ServiceErrorMapper.map {
            RegistryValueSeeder.ensureGlobalDefaults()
            ServiceResult.Ok(CacheManager.listGlobalValues())
        }
    }

    fun listPlayer(ownerId: String): ServiceResult<List<VariableValue>> {
        val normalizedOwner = ownerId.trim()
        if (normalizedOwner.isEmpty()) {
            return ServiceResult.Error(ServiceErrorCode.PLAYER_NOT_FOUND, arrayOf(ownerId))
        }

        return ServiceErrorMapper.map {
            if (!CacheManager.isPlayerCached(normalizedOwner)) {
                CacheManager.warmPlayer(normalizedOwner)
            }
            RegistryValueSeeder.ensurePlayerDefaults(normalizedOwner)
            ServiceResult.Ok(CacheManager.listPlayerValues(normalizedOwner).orEmpty())
        }
    }

    internal fun refreshGlobalDefault(key: String, raw: String, now: Long) {
        val current = CacheManager.getGlobalValue(key)
        val value = VariableValue(
            scope = VariableScope.GLOBAL,
            ownerId = OwnerIdResolver.globalOwnerId(),
            key = key,
            rawValue = raw,
            version = (current?.version ?: 0L) + 1L,
            updatedAt = now
        )
        SyncManager.enqueueValueUpsert(value)
        ApiEventPublisher.publishValueChanged(value)
    }

    private fun setValue(
        scope: VariableScope,
        ownerId: String,
        key: String,
        raw: String
    ): ServiceResult<VariableValue> {
        return ServiceErrorMapper.map {
            val registry = CacheManager.getRegistry(scope, key)
                ?: return@map ServiceResult.Error(
                    ServiceErrorCode.VARIABLE_NOT_REGISTERED,
                    arrayOf(scope.name, key)
                )

            if (!registry.enabled) {
                return@map ServiceResult.Error(ServiceErrorCode.REGISTRY_DISABLED, arrayOf(scope.name, key))
            }

            val normalized = DefaultTypeCodecRegistry.normalizeOrNull(registry.type, raw)
                ?: return@map ServiceResult.Error(
                    ServiceErrorCode.INVALID_TYPE_VALUE,
                    arrayOf(registry.type.name, raw)
                )

            val current = if (scope == VariableScope.GLOBAL) {
                CacheManager.getGlobalValue(key)
            } else {
                CacheManager.getPlayerValue(ownerId, key)
            }
            val value = VariableValue(
                scope = scope,
                ownerId = ownerId,
                key = key,
                rawValue = normalized,
                version = (current?.version ?: 0L) + 1L,
                updatedAt = System.currentTimeMillis()
            )
            SyncManager.enqueueValueUpsert(value)
            ApiEventPublisher.publishValueChanged(value)
            ServiceResult.Ok(value)
        }
    }

    private fun getValue(scope: VariableScope, ownerId: String, key: String): ServiceResult<String> {
        return ServiceErrorMapper.map {
            val registry = CacheManager.getRegistry(scope, key)
                ?: return@map ServiceResult.Error(
                    ServiceErrorCode.VARIABLE_NOT_REGISTERED,
                    arrayOf(scope.name, key)
                )

            if (!registry.enabled) {
                return@map ServiceResult.Error(ServiceErrorCode.REGISTRY_DISABLED, arrayOf(scope.name, key))
            }

            if (scope == VariableScope.GLOBAL) {
                RegistryValueSeeder.ensureGlobalDefaults()
            }
            if (scope == VariableScope.PLAYER && !CacheManager.isPlayerCached(ownerId)) {
                CacheManager.warmPlayer(ownerId)
            }
            if (scope == VariableScope.PLAYER) {
                RegistryValueSeeder.ensurePlayerDefaults(ownerId)
            }

            val current = if (scope == VariableScope.GLOBAL) {
                CacheManager.getGlobalValue(key)
            } else {
                CacheManager.getPlayerValue(ownerId, key)
            }

            val fallback = RegistryDefaultResolver.resolveForRead(registry)
            ServiceResult.Ok(current?.rawValue ?: fallback)
        }
    }

    private fun deleteValue(scope: VariableScope, ownerId: String, key: String): ServiceResult<Boolean> {
        return ServiceErrorMapper.map {
            val registry = CacheManager.getRegistry(scope, key)
                ?: return@map ServiceResult.Error(
                    ServiceErrorCode.VARIABLE_NOT_REGISTERED,
                    arrayOf(scope.name, key)
                )

            if (!registry.enabled) {
                return@map ServiceResult.Error(ServiceErrorCode.REGISTRY_DISABLED, arrayOf(scope.name, key))
            }

            SyncManager.enqueueValueDelete(scope, ownerId, key)
            ApiEventPublisher.publishDeleted(scope, ownerId, key)
            ServiceResult.Ok(true)
        }
    }
}

