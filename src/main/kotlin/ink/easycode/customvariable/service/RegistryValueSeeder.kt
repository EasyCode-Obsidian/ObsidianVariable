package ink.easycode.customvariable.service

import ink.easycode.customvariable.apiimpl.event.ApiEventPublisher
import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import ink.easycode.customvariable.sync.SyncManager
import ink.easycode.customvariable.util.OwnerIdResolver

object RegistryValueSeeder {

    fun seedOnRegister(entry: RegistryEntry) {
        if (!entry.enabled) {
            return
        }
        if (entry.scope == VariableScope.GLOBAL) {
            seedGlobal(entry)
            return
        }

        val owners = CacheManager.cachedPlayerOwnerIds()
        if (owners.isEmpty()) {
            return
        }

        val now = System.currentTimeMillis()
        owners.forEach { ownerId ->
            seedPlayerIfMissing(entry, ownerId, now)
        }
    }

    fun ensureGlobalDefaults() {
        val entries = CacheManager.listRegistries(VariableScope.GLOBAL)
        entries.forEach { entry ->
            if (!entry.enabled) {
                return@forEach
            }
            seedGlobal(entry)
        }
    }

    fun ensurePlayerDefaults(ownerId: String) {
        val normalizedOwner = ownerId.trim()
        if (normalizedOwner.isEmpty()) {
            return
        }

        val entries = CacheManager.listRegistries(VariableScope.PLAYER)
        if (entries.isEmpty()) {
            return
        }

        val now = System.currentTimeMillis()
        entries.forEach { entry ->
            if (!entry.enabled) {
                return@forEach
            }
            seedPlayerIfMissing(entry, normalizedOwner, now)
        }
    }

    private fun seedGlobal(entry: RegistryEntry) {
        if (CacheManager.getGlobalValue(entry.key) != null) {
            return
        }

        val now = System.currentTimeMillis()
        val value = VariableValue(
            scope = VariableScope.GLOBAL,
            ownerId = OwnerIdResolver.globalOwnerId(),
            key = entry.key,
            rawValue = RegistryDefaultResolver.resolveForRead(entry),
            version = 1L,
            updatedAt = now
        )
        SyncManager.enqueueValueUpsert(value)
        ApiEventPublisher.publishValueChanged(value)
    }

    private fun seedPlayerIfMissing(entry: RegistryEntry, ownerId: String, now: Long) {
        if (CacheManager.getPlayerValue(ownerId, entry.key) != null) {
            return
        }

        val value = VariableValue(
            scope = VariableScope.PLAYER,
            ownerId = ownerId,
            key = entry.key,
            rawValue = RegistryDefaultResolver.resolveForRead(entry),
            version = 1L,
            updatedAt = now
        )
        SyncManager.enqueueValueUpsert(value)
        ApiEventPublisher.publishValueChanged(value)
    }
}

