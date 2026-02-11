package ink.easycode.customvariable.model

data class VariableValue(
    val scope: VariableScope,
    val ownerId: String,
    val key: String,
    val rawValue: String,
    val version: Long,
    val updatedAt: Long
)
