package ink.easycode.customvariable.config

data class DatabaseConfig(
    val type: DatabaseType,
    val sqlite: SqliteConfig,
    val mysql: MysqlConfig,
    val pool: PoolConfig
)
