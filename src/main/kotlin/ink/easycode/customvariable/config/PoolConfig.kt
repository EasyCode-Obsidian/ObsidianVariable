package ink.easycode.customvariable.config

data class PoolConfig(
    val maximumPoolSize: Int,
    val minimumIdle: Int,
    val connectionTimeoutMs: Long
)
