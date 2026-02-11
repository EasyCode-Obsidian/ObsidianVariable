package ink.easycode.customvariable.sync

import ink.easycode.customvariable.model.VariableScope

data class ValueQueueKey(
    val scope: VariableScope,
    val ownerId: String,
    val key: String
)
