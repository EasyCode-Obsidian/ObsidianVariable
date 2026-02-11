package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope
import javax.sql.DataSource

class SqliteRegistryRepository(
    private val dataSource: DataSource
) : RegistryRepository {

    override fun upsert(entry: RegistryEntry): Boolean {
        val sql = """
            INSERT INTO cv_registry
            (scope, var_key, value_type, default_raw, description, enabled, version, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(scope, var_key) DO UPDATE SET
              value_type = excluded.value_type,
              default_raw = excluded.default_raw,
              description = excluded.description,
              enabled = excluded.enabled,
              version = excluded.version,
              updated_at = excluded.updated_at
        """.trimIndent()

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindEntry(statement, entry)
                statement.executeUpdate() > 0
            }
        }
    }
    override fun create(entry: RegistryEntry): Boolean {
        val sql = """
            INSERT OR IGNORE INTO cv_registry
            (scope, var_key, value_type, default_raw, description, enabled, version, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindEntry(statement, entry)
                statement.executeUpdate() > 0
            }
        }
    }

    override fun update(entry: RegistryEntry): Boolean {
        val sql = """
            UPDATE cv_registry
            SET value_type = ?, default_raw = ?, description = ?, enabled = ?, version = ?, updated_at = ?
            WHERE scope = ? AND var_key = ?
        """.trimIndent()

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, entry.type.name)
                statement.setString(2, entry.defaultRaw)
                statement.setString(3, entry.description)
                statement.setInt(4, if (entry.enabled) 1 else 0)
                statement.setLong(5, entry.version)
                statement.setLong(6, entry.updatedAt)
                statement.setString(7, entry.scope.name)
                statement.setString(8, entry.key)
                statement.executeUpdate() > 0
            }
        }
    }

    override fun delete(scope: VariableScope, key: String): Boolean {
        val sql = "DELETE FROM cv_registry WHERE scope = ? AND var_key = ?"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, scope.name)
                statement.setString(2, key)
                statement.executeUpdate() > 0
            }
        }
    }

    override fun find(scope: VariableScope, key: String): RegistryEntry? {
        val sql = "SELECT * FROM cv_registry WHERE scope = ? AND var_key = ? LIMIT 1"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, scope.name)
                statement.setString(2, key)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) RepositoryMapper.mapRegistry(resultSet) else null
                }
            }
        }
    }

    override fun list(scope: VariableScope?): List<RegistryEntry> {
        val sql = if (scope == null) {
            "SELECT * FROM cv_registry ORDER BY scope, var_key"
        } else {
            "SELECT * FROM cv_registry WHERE scope = ? ORDER BY var_key"
        }

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                if (scope != null) {
                    statement.setString(1, scope.name)
                }
                statement.executeQuery().use { resultSet ->
                    buildList {
                        while (resultSet.next()) {
                            add(RepositoryMapper.mapRegistry(resultSet))
                        }
                    }
                }
            }
        }
    }

    override fun pullUpdatedAfter(timestamp: Long): List<RegistryEntry> {
        val sql = "SELECT * FROM cv_registry WHERE updated_at > ? ORDER BY updated_at"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, timestamp)
                statement.executeQuery().use { resultSet ->
                    buildList {
                        while (resultSet.next()) {
                            add(RepositoryMapper.mapRegistry(resultSet))
                        }
                    }
                }
            }
        }
    }

    private fun bindEntry(statement: java.sql.PreparedStatement, entry: RegistryEntry) {
        statement.setString(1, entry.scope.name)
        statement.setString(2, entry.key)
        statement.setString(3, entry.type.name)
        statement.setString(4, entry.defaultRaw)
        statement.setString(5, entry.description)
        statement.setInt(6, if (entry.enabled) 1 else 0)
        statement.setLong(7, entry.version)
        statement.setLong(8, entry.updatedAt)
    }
}

