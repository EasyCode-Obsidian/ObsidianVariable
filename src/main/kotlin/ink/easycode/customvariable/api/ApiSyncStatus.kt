package ink.easycode.customvariable.api

data class ApiSyncStatus(
    val registryQueueSize: Int,
    val valueQueueSize: Int,
    val lastFlushAt: Long,
    val lastFlushCostMs: Long,
    val lastPullAt: Long,
    val lastPullCostMs: Long,
    val lastErrorAt: Long,
    val lastErrorMessage: String?,
    val consecutiveFailures: Int
)
