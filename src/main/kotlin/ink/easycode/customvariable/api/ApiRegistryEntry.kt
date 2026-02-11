package ink.easycode.customvariable.api

data class ApiRegistryEntry(
    val scope: ApiVariableScope,
    val key: String,
    val type: ApiVariableType,
    val defaultRaw: String?,
    val description: String?,
    val enabled: Boolean,
    val version: Long,
    val updatedAt: Long
)
