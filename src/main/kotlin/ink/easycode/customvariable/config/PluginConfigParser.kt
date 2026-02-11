package ink.easycode.customvariable.config

import taboolib.library.configuration.ConfigurationSection

object PluginConfigParser {

    fun parse(config: ConfigurationSection): PluginConfig {
        val database = parseDatabase(config)
        val sync = parseSync(config)
        val cache = parseCache(config)
        val runtime = parseRuntime(config)
        val placeholder = parsePlaceholder(config)

        return PluginConfig(database, sync, cache, runtime, placeholder)
    }

    private fun parseDatabase(config: ConfigurationSection): DatabaseConfig {
        val type = DatabaseType.fromValue(string(config, "database.type", "sqlite"))

        val sqlite = SqliteConfig(
            file = string(config, "database.sqlite.file", "data/variables.db")
        )

        val mysql = MysqlConfig(
            host = string(config, "database.mysql.host", "127.0.0.1"),
            port = config.getInt("database.mysql.port", 3306),
            database = string(config, "database.mysql.database", "customvariable"),
            username = string(config, "database.mysql.username", "root"),
            password = string(config, "database.mysql.password", "pass"),
            parameters = string(config, "database.mysql.parameters", "")
        )

        val pool = PoolConfig(
            maximumPoolSize = config.getInt("database.pool.maximumPoolSize", 10),
            minimumIdle = config.getInt("database.pool.minimumIdle", 2),
            connectionTimeoutMs = config.getLong("database.pool.connectionTimeoutMs", 10000L)
        )

        return DatabaseConfig(type, sqlite, mysql, pool)
    }

    private fun parseSync(config: ConfigurationSection): SyncConfig {
        return SyncConfig(
            intervalSeconds = config.getInt("sync.intervalSeconds", 30),
            batchSize = config.getInt("sync.batchSize", 200)
        )
    }

    private fun parseCache(config: ConfigurationSection): CacheConfig {
        return CacheConfig(
            playerTtlSeconds = config.getInt("cache.playerTtlSeconds", 600),
            playerMaxEntries = config.getInt("cache.playerMaxEntries", 5000)
        )
    }

    private fun parseRuntime(config: ConfigurationSection): RuntimeConfig {
        return RuntimeConfig(
            flushOnDisable = config.getBoolean("runtime.flushOnDisable", true)
        )
    }

    private fun parsePlaceholder(config: ConfigurationSection): PlaceholderConfig {
        return PlaceholderConfig(
            missingPolicy = string(config, "placeholder.missingPolicy", "empty"),
            missingLiteral = string(config, "placeholder.missingLiteral", "")
        )
    }

    private fun string(config: ConfigurationSection, path: String, default: String): String {
        return config.getString(path, default) ?: default
    }
}
