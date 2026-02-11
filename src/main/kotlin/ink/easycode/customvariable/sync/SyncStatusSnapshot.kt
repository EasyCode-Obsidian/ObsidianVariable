package ink.easycode.customvariable.sync

data class SyncStatusSnapshot(
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
