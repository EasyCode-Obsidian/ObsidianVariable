package ink.easycode.customvariable.sync

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.RegistryKey

data class RegistryQueueEntry(
    val key: RegistryKey,
    val action: RegistryWriteAction,
    val entry: RegistryEntry?,
    val attempts: Int,
    val nextRetryAt: Long,
    val version: Long,
    val updatedAt: Long
)
