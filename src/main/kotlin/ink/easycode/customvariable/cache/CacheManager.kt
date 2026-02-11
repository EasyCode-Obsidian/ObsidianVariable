package ink.easycode.customvariable.cache

import ink.easycode.customvariable.config.PluginConfig
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import ink.easycode.customvariable.repository.RegistryRepository
import ink.easycode.customvariable.repository.ValueRepository
import ink.easycode.customvariable.util.OwnerIdResolver
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning

object CacheManager {

    private val registryCache = RegistryCache()
    private val globalValueCache = GlobalValueCache()

    @Volatile
    private var playerValueCache: PlayerValueCache? = null

    @Volatile
    private var registryRepositoryRef: RegistryRepository? = null

    @Volatile
    private var valueRepositoryRef: ValueRepository? = null

    fun start(config: PluginConfig, registryRepository: RegistryRepository, valueRepository: ValueRepository) {
        registryRepositoryRef = registryRepository
        valueRepositoryRef = valueRepository
        playerValueCache = PlayerValueCache(config.cache.playerTtlSeconds, config.cache.playerMaxEntries)
        rebuildAll()
        info(
            "[CustomVariable] Cache initialized: registry=${registryCache.size()}, global=${globalValueCache.size()}"
        )
    }

    fun stop() {
        registryCache.clear()
        globalValueCache.clear()
        playerValueCache?.clear()
        playerValueCache = null
        registryRepositoryRef = null
        valueRepositoryRef = null
    }

    fun rebuildAll() {
        val registryRepository = registryRepository()
        val valueRepository = valueRepository()

        val registries = registryRepository.list(null)
        val globals = valueRepository.listByOwner(
            VariableScope.GLOBAL,
            OwnerIdResolver.globalOwnerId()
        )

        registryCache.rebuild(registries)
        globalValueCache.rebuild(globals)
        playerCache().clear()
    }

    fun warmPlayer(ownerId: String) {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return
        }
        val values = valueRepository().listByOwner(VariableScope.PLAYER, normalized)
        playerCache().putAll(normalized, values)
    }

    fun warmPlayerAsync(ownerId: String) {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return
        }
        submitAsync {
            runCatching { warmPlayer(normalized) }.onFailure { ex ->
                warning("[CustomVariable] Failed to warm player cache for $normalized: ${ex.message}")
            }
        }
    }

    fun getRegistry(scope: VariableScope, key: String): RegistryEntry? {
        return registryCache.get(scope, key)
    }

    fun listRegistries(scope: VariableScope?): List<RegistryEntry> {
        return registryCache.list(scope).sortedBy { "${it.scope.name}:${it.key}" }
    }

    fun getGlobalValue(key: String): VariableValue? {
        return globalValueCache.get(key)
    }

    fun listGlobalValues(): List<VariableValue> {
        return globalValueCache.list().sortedBy { it.key }
    }

    fun getPlayerValue(ownerId: String, key: String): VariableValue? {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return null
        }
        return playerCache().get(normalized, key)
    }

    fun listPlayerValues(ownerId: String): List<VariableValue>? {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return null
        }
        return playerCache().listOwner(normalized)?.sortedBy { it.key }
    }

    fun isPlayerCached(ownerId: String): Boolean {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return false
        }
        return playerCache().hasOwner(normalized)
    }

    fun cacheRegistry(entry: RegistryEntry) {
        registryCache.put(entry)
    }

    fun removeRegistry(scope: VariableScope, key: String) {
        registryCache.remove(scope, key)
    }

    fun cacheGlobalValue(value: VariableValue) {
        globalValueCache.put(value)
    }

    fun removeGlobalValue(key: String) {
        globalValueCache.remove(key)
    }

    fun cachePlayerValue(value: VariableValue) {
        playerCache().put(value)
    }

    fun removePlayerValue(ownerId: String, key: String) {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return
        }
        playerCache().remove(normalized, key)
    }

    fun invalidateByScopeAndKey(scope: VariableScope, key: String) {
        registryCache.remove(scope, key)
        if (scope == VariableScope.GLOBAL) {
            globalValueCache.remove(key)
        } else {
            playerCache().invalidateByKey(key)
        }
    }

    fun invalidateByKey(key: String) {
        registryCache.invalidateByKey(key)
        globalValueCache.invalidateByKey(key)
        playerCache().invalidateByKey(key)
    }

    fun invalidatePlayer(ownerId: String) {
        val normalized = ownerId.trim()
        if (normalized.isEmpty()) {
            return
        }
        playerCache().invalidateOwner(normalized)
    }

    fun evictExpiredPlayers() {
        playerCache().evictExpired()
    }

    fun playerCacheOwners(): Int {
        return playerCache().ownerCount()
    }

    fun cachedPlayerOwnerIds(): List<String> {
        return playerCache().ownerIds()
    }

    private fun playerCache(): PlayerValueCache {
        return playerValueCache ?: error("Player cache is not initialized.")
    }

    private fun registryRepository(): RegistryRepository {
        return registryRepositoryRef ?: error("RegistryRepository is not initialized.")
    }

    private fun valueRepository(): ValueRepository {
        return valueRepositoryRef ?: error("ValueRepository is not initialized.")
    }
}

