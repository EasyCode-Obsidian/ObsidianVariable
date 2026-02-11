package ink.easycode.customvariable.sync

object SyncMetrics {

    @Volatile
    private var lastFlushAt: Long = 0L

    @Volatile
    private var lastFlushCostMs: Long = 0L

    @Volatile
    private var lastPullAt: Long = 0L

    @Volatile
    private var lastPullCostMs: Long = 0L

    @Volatile
    private var lastErrorAt: Long = 0L

    @Volatile
    private var lastErrorMessage: String? = null

    @Volatile
    private var consecutiveFailures: Int = 0

    fun recordFlush(costMs: Long) {
        lastFlushAt = System.currentTimeMillis()
        lastFlushCostMs = costMs
    }

    fun recordPull(costMs: Long) {
        lastPullAt = System.currentTimeMillis()
        lastPullCostMs = costMs
    }

    fun recordFailure(message: String) {
        lastErrorAt = System.currentTimeMillis()
        lastErrorMessage = message
        consecutiveFailures += 1
    }

    fun clearFailures() {
        consecutiveFailures = 0
    }

    fun snapshot(registryQueueSize: Int, valueQueueSize: Int): SyncStatusSnapshot {
        return SyncStatusSnapshot(
            registryQueueSize = registryQueueSize,
            valueQueueSize = valueQueueSize,
            lastFlushAt = lastFlushAt,
            lastFlushCostMs = lastFlushCostMs,
            lastPullAt = lastPullAt,
            lastPullCostMs = lastPullCostMs,
            lastErrorAt = lastErrorAt,
            lastErrorMessage = lastErrorMessage,
            consecutiveFailures = consecutiveFailures
        )
    }
}
