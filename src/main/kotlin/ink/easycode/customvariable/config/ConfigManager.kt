package ink.easycode.customvariable.config

import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration

object ConfigManager {

    @Volatile
    private var current: PluginConfig? = null

    fun load(): PluginConfig {
        val file = releaseResourceFile("config.yml")
        val configuration = Configuration.Companion.loadFromFile(file)
        val parsed = PluginConfigParser.parse(configuration)
        current = parsed
        logSummary(parsed)
        return parsed
    }

    fun current(): PluginConfig {
        return current ?: load()
    }

    private fun logSummary(config: PluginConfig) {
        val db = config.database
        val sync = config.sync
        val cache = config.cache
        info("[CustomVariable] Config loaded: dbType=${db.type.name.lowercase()}, sync=${sync.intervalSeconds}s, batch=${sync.batchSize}, cacheTtl=${cache.playerTtlSeconds}s")
    }
}
