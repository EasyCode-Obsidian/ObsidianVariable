package ink.easycode.customvariable.api

data class ApiVariableValue(
    val scope: ApiVariableScope,
    val ownerId: String,
    val key: String,
    val rawValue: String,
    val version: Long,
    val updatedAt: Long
)
