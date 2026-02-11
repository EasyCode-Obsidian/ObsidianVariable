package ink.easycode.customvariable.model

data class RegistryEntry(
    val scope: VariableScope,
    val key: String,
    val type: VariableType,
    val defaultRaw: String?,
    val description: String?,
    val enabled: Boolean,
    val version: Long,
    val updatedAt: Long
)
