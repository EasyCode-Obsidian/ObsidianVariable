package ink.easycode.customvariable.repository

import ink.easycode.customvariable.config.PluginConfig
import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.info
import javax.sql.DataSource

object DatabaseManager {

    @Volatile
    private var dataSource: DataSource? = null

    fun start(config: PluginConfig): DataSource {
        val created = DataSourceFactory.create(config)
        return try {
            SchemaManager.bootstrap(created, config.database.type)
            dataSource = created
            info("[CustomVariable] Database initialized: ${config.database.type.name.lowercase()}")
            created
        } catch (ex: SchemaMigrationException) {
            closeIfPossible(created)
            val reason = ex.cause?.message ?: ex.message ?: "unknown"
            CustomVariableLang.sendToConsole(
                LangKeys.DATABASE_MIGRATION_FAILED,
                config.database.type.name.lowercase(),
                reason
            )
            disablePlugin()
            throw ex
        }
    }

    fun current(): DataSource {
        return dataSource ?: error("DataSource is not initialized.")
    }

    fun stop() {
        val current = dataSource ?: return
        closeIfPossible(current)
        dataSource = null
        info("[CustomVariable] Database connection closed.")
    }

    private fun closeIfPossible(source: DataSource) {
        val closeable = source as? AutoCloseable
        closeable?.close()
    }
}
