package ink.easycode.customvariable.bootstrap

import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.config.ConfigManager
import ink.easycode.customvariable.repository.DatabaseManager
import ink.easycode.customvariable.repository.RepositoryManager
import ink.easycode.customvariable.sync.SyncManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning

object PluginBootstrap {

    @Volatile
    private var ready = false

    @Awake(LifeCycle.ENABLE)
    fun onEnable() {
        start()
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        stop()
    }

    fun start() {
        if (ready) {
            return
        }
        ready = false

        val config = ConfigManager.load()
        val dataSource = DatabaseManager.start(config)
        RepositoryManager.start(config, dataSource)
        CacheManager.start(config, RepositoryManager.registry(), RepositoryManager.value())
        SyncManager.start(config, RepositoryManager.registry(), RepositoryManager.value())
        ready = true
        info("[CustomVariable] Bootstrap start completed.")
    }

    fun reload(): Boolean {
        ready = false
        return runCatching {
            val current = ConfigManager.current()
            SyncManager.stop(current.runtime.flushOnDisable)
            CacheManager.stop()
            RepositoryManager.stop()
            DatabaseManager.stop()

            val config = ConfigManager.load()
            val dataSource = DatabaseManager.start(config)
            RepositoryManager.start(config, dataSource)
            CacheManager.start(config, RepositoryManager.registry(), RepositoryManager.value())
            SyncManager.start(config, RepositoryManager.registry(), RepositoryManager.value())
            ready = true
            info("[CustomVariable] Bootstrap reload completed.")
        }.onFailure { ex ->
            warning("[CustomVariable] Bootstrap reload failed: ${ex.message}")
            ready = false
        }.isSuccess
    }

    fun stop() {
        ready = false
        val config = ConfigManager.current()
        SyncManager.stop(config.runtime.flushOnDisable)
        CacheManager.stop()
        RepositoryManager.stop()
        DatabaseManager.stop()
        info("[CustomVariable] Bootstrap stop completed.")
    }

    fun isReady(): Boolean {
        return ready
    }
}
