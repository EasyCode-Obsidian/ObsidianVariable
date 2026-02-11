package ink.easycode.customvariable.sync

import ink.easycode.customvariable.model.VariableValue

data class ValueQueueEntry(
    val key: ValueQueueKey,
    val action: ValueWriteAction,
    val value: VariableValue?,
    val attempts: Int,
    val nextRetryAt: Long,
    val version: Long,
    val updatedAt: Long
)
