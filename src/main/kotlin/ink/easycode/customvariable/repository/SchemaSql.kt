package ink.easycode.customvariable.repository

import ink.easycode.customvariable.config.DatabaseType

object SchemaSql {

    fun createRegistryTable(type: DatabaseType): String {
        return if (type == DatabaseType.SQLITE) {
            """
            CREATE TABLE IF NOT EXISTS cv_registry (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              scope TEXT NOT NULL,
              var_key TEXT NOT NULL,
              value_type TEXT NOT NULL,
              default_raw TEXT NULL,
              description TEXT NULL,
              enabled INTEGER NOT NULL DEFAULT 1,
              version INTEGER NOT NULL DEFAULT 0,
              updated_at INTEGER NOT NULL,
              UNIQUE(scope, var_key)
            )
            """.trimIndent()
        } else {
            """
            CREATE TABLE IF NOT EXISTS cv_registry (
              id BIGINT NOT NULL AUTO_INCREMENT,
              scope VARCHAR(16) NOT NULL,
              var_key VARCHAR(64) NOT NULL,
              value_type VARCHAR(32) NOT NULL,
              default_raw TEXT NULL,
              description VARCHAR(255) NULL,
              enabled TINYINT NOT NULL DEFAULT 1,
              version BIGINT NOT NULL DEFAULT 0,
              updated_at BIGINT NOT NULL,
              PRIMARY KEY (id),
              UNIQUE KEY uk_registry_scope_key (scope, var_key)
            )
            """.trimIndent()
        }
    }

    fun createValueTable(type: DatabaseType): String {
        return if (type == DatabaseType.SQLITE) {
            """
            CREATE TABLE IF NOT EXISTS cv_value (
              scope TEXT NOT NULL,
              owner_id TEXT NOT NULL,
              var_key TEXT NOT NULL,
              raw_value TEXT NOT NULL,
              version INTEGER NOT NULL DEFAULT 0,
              updated_at INTEGER NOT NULL,
              PRIMARY KEY (scope, owner_id, var_key)
            )
            """.trimIndent()
        } else {
            """
            CREATE TABLE IF NOT EXISTS cv_value (
              scope VARCHAR(16) NOT NULL,
              owner_id VARCHAR(64) NOT NULL,
              var_key VARCHAR(64) NOT NULL,
              raw_value TEXT NOT NULL,
              version BIGINT NOT NULL DEFAULT 0,
              updated_at BIGINT NOT NULL,
              PRIMARY KEY (scope, owner_id, var_key)
            )
            """.trimIndent()
        }
    }

    fun createValueIndex(type: DatabaseType): String {
        return if (type == DatabaseType.SQLITE) {
            "CREATE INDEX IF NOT EXISTS idx_cv_value_scope_key_updated ON cv_value(scope, var_key, updated_at)"
        } else {
            "CREATE INDEX idx_cv_value_scope_key_updated ON cv_value(scope, var_key, updated_at)"
        }
    }

    fun createSchemaVersionTable(type: DatabaseType): String {
        return if (type == DatabaseType.SQLITE) {
            """
            CREATE TABLE IF NOT EXISTS cv_schema_version (
              id INTEGER PRIMARY KEY,
              version INTEGER NOT NULL,
              updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        } else {
            """
            CREATE TABLE IF NOT EXISTS cv_schema_version (
              id INT NOT NULL,
              version BIGINT NOT NULL,
              updated_at BIGINT NOT NULL,
              PRIMARY KEY (id)
            )
            """.trimIndent()
        }
    }
}
