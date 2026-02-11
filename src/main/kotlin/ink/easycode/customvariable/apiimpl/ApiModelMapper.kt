package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiRegistryEntry
import ink.easycode.customvariable.api.ApiSyncStatus
import ink.easycode.customvariable.api.ApiVariableValue
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableValue
import ink.easycode.customvariable.sync.SyncStatusSnapshot

object ApiModelMapper {

    fun mapRegistry(entry: RegistryEntry): ApiRegistryEntry {
        return ApiRegistryEntry(
            scope = ApiScopeMapper.fromModel(entry.scope),
            key = entry.key,
            type = ApiTypeMapper.fromModel(entry.type),
            defaultRaw = entry.defaultRaw,
            description = entry.description,
            enabled = entry.enabled,
            version = entry.version,
            updatedAt = entry.updatedAt
        )
    }

    fun mapValue(value: VariableValue): ApiVariableValue {
        return ApiVariableValue(
            scope = ApiScopeMapper.fromModel(value.scope),
            ownerId = value.ownerId,
            key = value.key,
            rawValue = value.rawValue,
            version = value.version,
            updatedAt = value.updatedAt
        )
    }

    fun mapSync(status: SyncStatusSnapshot): ApiSyncStatus {
        return ApiSyncStatus(
            registryQueueSize = status.registryQueueSize,
            valueQueueSize = status.valueQueueSize,
            lastFlushAt = status.lastFlushAt,
            lastFlushCostMs = status.lastFlushCostMs,
            lastPullAt = status.lastPullAt,
            lastPullCostMs = status.lastPullCostMs,
            lastErrorAt = status.lastErrorAt,
            lastErrorMessage = status.lastErrorMessage,
            consecutiveFailures = status.consecutiveFailures
        )
    }
}
