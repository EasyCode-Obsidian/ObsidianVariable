package ink.easycode.customvariable.config

data class PluginConfig(
    val database: DatabaseConfig,
    val sync: SyncConfig,
    val cache: CacheConfig,
    val runtime: RuntimeConfig,
    val placeholder: PlaceholderConfig
)
