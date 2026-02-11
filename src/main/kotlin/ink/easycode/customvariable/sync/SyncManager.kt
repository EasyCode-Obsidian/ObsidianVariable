package ink.easycode.customvariable.sync

import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.config.PluginConfig
import ink.easycode.customvariable.config.SyncConfig
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import ink.easycode.customvariable.repository.RegistryRepository
import ink.easycode.customvariable.repository.ValueRepository
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor.PlatformTask

object SyncManager {

    private val queueStore = SyncQueueStore()
    private val cursor = SyncCursor()

    @Volatile
    private var syncConfigRef: SyncConfig? = null

    @Volatile
    private var registryRepositoryRef: RegistryRepository? = null

    @Volatile
    private var valueRepositoryRef: ValueRepository? = null

    @Volatile
    private var periodicTask: PlatformTask? = null

    fun start(config: PluginConfig, registryRepository: RegistryRepository, valueRepository: ValueRepository) {
        syncConfigRef = config.sync
        registryRepositoryRef = registryRepository
        valueRepositoryRef = valueRepository
        cursor.lastRegistryPullAt = System.currentTimeMillis()
        cursor.lastValuePullAt = System.currentTimeMillis()
        startPeriodicTask(config.sync.intervalSeconds, config.sync.batchSize)
        info("[CustomVariable] Sync started: interval=${config.sync.intervalSeconds}s, batch=${config.sync.batchSize}")
    }

    fun stop(flushOnDisable: Boolean) {
        periodicTask?.cancel()
        periodicTask = null
        val registry = registryRepositoryRef
        val value = valueRepositoryRef
        if (flushOnDisable && registry != null && value != null) {
            SyncFlushExecutor.flushAllPending(queueStore, registry, value)
        }
        queueStore.clear()
        syncConfigRef = null
        registryRepositoryRef = null
        valueRepositoryRef = null
    }

    fun enqueueRegistryUpsert(entry: RegistryEntry) {
        CacheManager.cacheRegistry(entry)
        queueStore.enqueueRegistryUpsert(entry)
    }

    fun enqueueRegistryDelete(scope: VariableScope, key: String) {
        val now = System.currentTimeMillis()
        CacheManager.invalidateByScopeAndKey(scope, key)
        queueStore.enqueueRegistryDelete(scope, key, now, now)
    }

    fun enqueueValueUpsert(value: VariableValue) {
        if (value.scope == VariableScope.GLOBAL) {
            CacheManager.cacheGlobalValue(value)
        } else {
            CacheManager.cachePlayerValue(value)
        }
        queueStore.enqueueValueUpsert(value)
    }

    fun enqueueValueDelete(scope: VariableScope, ownerId: String, key: String) {
        val now = System.currentTimeMillis()
        if (scope == VariableScope.GLOBAL) {
            CacheManager.removeGlobalValue(key)
        } else {
            CacheManager.removePlayerValue(ownerId, key)
        }
        queueStore.enqueueValueDelete(scope, ownerId, key, now, now)
    }

    fun forceSyncNow() {
        runCycle(syncConfig().batchSize)
    }

    fun status(): SyncStatusSnapshot {
        return SyncMetrics.snapshot(queueStore.registrySize(), queueStore.valueSize())
    }

    private fun startPeriodicTask(intervalSeconds: Int, batchSize: Int) {
        val intervalTicks = intervalSeconds.coerceAtLeast(1).toLong() * 20L
        periodicTask?.cancel()
        periodicTask = submitAsync(false, intervalTicks, intervalTicks) {
            runCycle(batchSize)
        }
    }

    private fun runCycle(batchSize: Int) {
        val flushStart = System.currentTimeMillis()
        val flushSuccess = SyncFlushExecutor.flushOnce(
            queueStore,
            registryRepository(),
            valueRepository(),
            batchSize
        )
        SyncMetrics.recordFlush(System.currentTimeMillis() - flushStart)

        val pullStart = System.currentTimeMillis()
        val pullSuccess = SyncPullExecutor.pullOnce(cursor, registryRepository(), valueRepository())
        SyncMetrics.recordPull(System.currentTimeMillis() - pullStart)

        CacheManager.evictExpiredPlayers()
        if (flushSuccess && pullSuccess) {
            SyncMetrics.clearFailures()
        }
    }

    private fun syncConfig(): SyncConfig {
        return syncConfigRef ?: error("SyncConfig is not initialized.")
    }

    private fun registryRepository(): RegistryRepository {
        return registryRepositoryRef ?: error("RegistryRepository is not initialized.")
    }

    private fun valueRepository(): ValueRepository {
        return valueRepositoryRef ?: error("ValueRepository is not initialized.")
    }
}
