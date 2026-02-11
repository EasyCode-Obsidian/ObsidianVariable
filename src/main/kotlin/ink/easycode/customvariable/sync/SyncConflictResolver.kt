package ink.easycode.customvariable.sync

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableValue

object SyncConflictResolver {

    fun shouldApplyRegistry(local: RegistryEntry?, incoming: RegistryEntry): Boolean {
        if (local == null) {
            return true
        }
        return shouldApply(local.version, local.updatedAt, incoming.version, incoming.updatedAt)
    }

    fun shouldApplyValue(local: VariableValue?, incoming: VariableValue): Boolean {
        if (local == null) {
            return true
        }
        return shouldApply(local.version, local.updatedAt, incoming.version, incoming.updatedAt)
    }

    private fun shouldApply(
        localVersion: Long,
        localUpdatedAt: Long,
        incomingVersion: Long,
        incomingUpdatedAt: Long
    ): Boolean {
        return when {
            incomingVersion > localVersion -> true
            incomingVersion < localVersion -> false
            else -> incomingUpdatedAt >= localUpdatedAt
        }
    }
}
