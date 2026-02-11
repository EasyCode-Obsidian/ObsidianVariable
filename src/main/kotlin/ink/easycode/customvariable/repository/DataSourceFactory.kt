package ink.easycode.customvariable.repository

import ink.easycode.customvariable.config.DatabaseType
import ink.easycode.customvariable.config.PluginConfig
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.Database
import taboolib.module.database.Host
import taboolib.module.database.HostSQL
import taboolib.module.database.HostSQLite
import java.io.File
import javax.sql.DataSource

object DataSourceFactory {

    fun create(config: PluginConfig): DataSource {
        return when (config.database.type) {
            DatabaseType.SQLITE -> createSqlite(config)
            DatabaseType.MYSQL -> createMysql(config)
        }
    }

    private fun createSqlite(config: PluginConfig): DataSource {
        val dbFile = File(getDataFolder(), config.database.sqlite.file)
        dbFile.parentFile?.mkdirs()
        val host = HostSQLite(dbFile)
        return createWithPool(host, config)
    }

    private fun createMysql(config: PluginConfig): DataSource {
        val mysql = config.database.mysql
        val host = HostSQL(
            mysql.host,
            mysql.port.toString(),
            mysql.username,
            mysql.password,
            mysql.database
        )
        host.flags.clear()
        host.flags.addAll(parseFlags(mysql.parameters))
        return createWithPool(host, config)
    }

    private fun createWithPool(host: Host<*>, config: PluginConfig): DataSource {
        applyPoolSettings(config)
        return host.createDataSource(autoRelease = true, withoutConfig = false)
    }

    private fun applyPoolSettings(config: PluginConfig) {
        val pool = config.database.pool
        val settings = Database.settingsFile

        settings.set("DefaultSettings.MaximumPoolSize", pool.maximumPoolSize)
        settings.set("DefaultSettings.MinimumIdle", pool.minimumIdle)
        settings.set("DefaultSettings.ConnectionTimeout", pool.connectionTimeoutMs)
    }

    private fun parseFlags(parameters: String): List<String> {
        if (parameters.isBlank()) {
            return emptyList()
        }
        return parameters.split("&").mapNotNull { entry ->
            val value = entry.trim()
            if (value.isBlank()) null else value
        }
    }
}
