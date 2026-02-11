package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import javax.sql.DataSource

class SqliteValueRepository(
    private val dataSource: DataSource
) : ValueRepository {

    override fun upsert(value: VariableValue): Boolean {
        val sql = """
            INSERT INTO cv_value (scope, owner_id, var_key, raw_value, version, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT(scope, owner_id, var_key) DO UPDATE SET
              raw_value = excluded.raw_value,
              version = excluded.version,
              updated_at = excluded.updated_at
        """.trimIndent()

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindValue(statement, value)
                statement.executeUpdate() > 0
            }
        }
    }

    override fun delete(scope: VariableScope, ownerId: String, key: String): Boolean {
        val sql = "DELETE FROM cv_value WHERE scope = ? AND owner_id = ? AND var_key = ?"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, scope.name)
                statement.setString(2, ownerId)
                statement.setString(3, key)
                statement.executeUpdate() > 0
            }
        }
    }

    override fun deleteByKey(scope: VariableScope, key: String): Int {
        val sql = "DELETE FROM cv_value WHERE scope = ? AND var_key = ?"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, scope.name)
                statement.setString(2, key)
                statement.executeUpdate()
            }
        }
    }

    override fun find(scope: VariableScope, ownerId: String, key: String): VariableValue? {
        val sql = "SELECT * FROM cv_value WHERE scope = ? AND owner_id = ? AND var_key = ? LIMIT 1"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, scope.name)
                statement.setString(2, ownerId)
                statement.setString(3, key)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) RepositoryMapper.mapValue(resultSet) else null
                }
            }
        }
    }

    override fun listByOwner(scope: VariableScope, ownerId: String): List<VariableValue> {
        val sql = "SELECT * FROM cv_value WHERE scope = ? AND owner_id = ? ORDER BY var_key"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, scope.name)
                statement.setString(2, ownerId)
                statement.executeQuery().use { resultSet ->
                    buildList {
                        while (resultSet.next()) {
                            add(RepositoryMapper.mapValue(resultSet))
                        }
                    }
                }
            }
        }
    }

    override fun pullUpdatedAfter(timestamp: Long): List<VariableValue> {
        val sql = "SELECT * FROM cv_value WHERE updated_at > ? ORDER BY updated_at"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, timestamp)
                statement.executeQuery().use { resultSet ->
                    buildList {
                        while (resultSet.next()) {
                            add(RepositoryMapper.mapValue(resultSet))
                        }
                    }
                }
            }
        }
    }

    override fun resetAllByKey(scope: VariableScope, key: String, rawValue: String): Int {
        val sql = "UPDATE cv_value SET raw_value = ?, updated_at = ? WHERE scope = ? AND var_key = ?"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, rawValue)
                statement.setLong(2, System.currentTimeMillis())
                statement.setString(3, scope.name)
                statement.setString(4, key)
                statement.executeUpdate()
            }
        }
    }

    private fun bindValue(statement: java.sql.PreparedStatement, value: VariableValue) {
        statement.setString(1, value.scope.name)
        statement.setString(2, value.ownerId)
        statement.setString(3, value.key)
        statement.setString(4, value.rawValue)
        statement.setLong(5, value.version)
        statement.setLong(6, value.updatedAt)
    }
}
