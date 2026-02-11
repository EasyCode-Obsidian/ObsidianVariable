package ink.easycode.customvariable.sync

import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import ink.easycode.customvariable.repository.RegistryRepository
import ink.easycode.customvariable.repository.ValueRepository
import taboolib.common.platform.function.warning

object SyncPullExecutor {

    fun pullOnce(
        cursor: SyncCursor,
        registryRepository: RegistryRepository,
        valueRepository: ValueRepository
    ): Boolean {
        return runCatching {
            pullRegistries(cursor, registryRepository)
            pullValues(cursor, valueRepository)
            true
        }.getOrElse { ex ->
            onPullFailure(ex)
            false
        }
    }

    private fun pullRegistries(cursor: SyncCursor, registryRepository: RegistryRepository) {
        val pulled = registryRepository.pullUpdatedAfter(cursor.lastRegistryPullAt)
            .sortedBy { it.updatedAt }
        pulled.forEach { entry ->
            val local = CacheManager.getRegistry(entry.scope, entry.key)
            if (SyncConflictResolver.shouldApplyRegistry(local, entry)) {
                CacheManager.cacheRegistry(entry)
            }
            if (entry.updatedAt > cursor.lastRegistryPullAt) {
                cursor.lastRegistryPullAt = entry.updatedAt
            }
        }
    }

    private fun pullValues(cursor: SyncCursor, valueRepository: ValueRepository) {
        val pulled = valueRepository.pullUpdatedAfter(cursor.lastValuePullAt)
            .sortedBy { it.updatedAt }
        pulled.forEach { value ->
            if (!shouldApplyPulledValue(value)) {
                updateValueCursor(cursor, value)
                return@forEach
            }

            val local = if (value.scope == VariableScope.GLOBAL) {
                CacheManager.getGlobalValue(value.key)
            } else {
                CacheManager.getPlayerValue(value.ownerId, value.key)
            }
            if (SyncConflictResolver.shouldApplyValue(local, value)) {
                if (value.scope == VariableScope.GLOBAL) {
                    CacheManager.cacheGlobalValue(value)
                } else {
                    CacheManager.cachePlayerValue(value)
                }
            }
            updateValueCursor(cursor, value)
        }
    }

    private fun shouldApplyPulledValue(value: VariableValue): Boolean {
        return value.scope == VariableScope.GLOBAL || CacheManager.isPlayerCached(value.ownerId)
    }

    private fun updateValueCursor(cursor: SyncCursor, value: VariableValue) {
        if (value.updatedAt > cursor.lastValuePullAt) {
            cursor.lastValuePullAt = value.updatedAt
        }
    }

    private fun onPullFailure(ex: Throwable) {
        val reason = ex.message ?: ex::class.java.simpleName
        SyncMetrics.recordFailure("pull:$reason")
        warning("[CustomVariable] Incremental pull failed: $reason")
        CustomVariableLang.sendToConsole(LangKeys.SYNC_PULL_FAILED, reason)
    }
}
