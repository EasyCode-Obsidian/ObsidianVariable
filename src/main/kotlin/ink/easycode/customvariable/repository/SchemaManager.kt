package ink.easycode.customvariable.repository

import ink.easycode.customvariable.config.DatabaseType
import java.sql.Connection
import javax.sql.DataSource

object SchemaManager {

    fun bootstrap(dataSource: DataSource, type: DatabaseType) {
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            try {
                executeDDL(connection, SchemaSql.createSchemaVersionTable(type))
                executeDDL(connection, SchemaSql.createRegistryTable(type))
                executeDDL(connection, SchemaSql.createValueTable(type))
                executeDDL(connection, SchemaSql.createValueIndex(type))
                migrateIfNeeded(connection)
                connection.commit()
            } catch (ex: Exception) {
                rollbackQuietly(connection)
                throw SchemaMigrationException("Failed to bootstrap schema.", ex)
            } finally {
                runCatching { connection.autoCommit = true }
            }
        }
    }

    private fun executeDDL(connection: Connection, sql: String) {
        connection.createStatement().use { statement ->
            statement.execute(sql)
        }
    }

    private fun migrateIfNeeded(connection: Connection) {
        val current = currentVersion(connection)

        when {
            current == null -> insertVersion(connection, SchemaVersion.CURRENT)
            current < SchemaVersion.CURRENT -> {
                applyMigrations(connection, current, SchemaVersion.CURRENT)
                updateVersion(connection, SchemaVersion.CURRENT)
            }
            current > SchemaVersion.CURRENT -> {
                throw IllegalStateException("Database schema version is newer than plugin schema.")
            }
        }
    }

    private fun currentVersion(connection: Connection): Long? {
        connection.prepareStatement("SELECT version FROM cv_schema_version WHERE id = 1").use { statement ->
            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) resultSet.getLong(1) else null
            }
        }
    }

    private fun insertVersion(connection: Connection, version: Long) {
        val now = System.currentTimeMillis()
        connection.prepareStatement(
            "INSERT INTO cv_schema_version (id, version, updated_at) VALUES (1, ?, ?)"
        ).use { statement ->
            statement.setLong(1, version)
            statement.setLong(2, now)
            statement.executeUpdate()
        }
    }

    private fun updateVersion(connection: Connection, version: Long) {
        val now = System.currentTimeMillis()
        connection.prepareStatement(
            "UPDATE cv_schema_version SET version = ?, updated_at = ? WHERE id = 1"
        ).use { statement ->
            statement.setLong(1, version)
            statement.setLong(2, now)
            statement.executeUpdate()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun applyMigrations(connection: Connection, from: Long, to: Long) {
        if (from == to) {
            return
        }
        // Placeholder for incremental SQL migrations in future schema versions.
    }

    private fun rollbackQuietly(connection: Connection) {
        runCatching { connection.rollback() }
    }
}
