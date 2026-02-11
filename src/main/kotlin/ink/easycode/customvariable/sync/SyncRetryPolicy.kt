package ink.easycode.customvariable.sync

object SyncRetryPolicy {

    private const val BASE_DELAY_MS = 500L
    private const val MAX_DELAY_MS = 60_000L

    fun nextDelayMillis(nextAttempt: Int): Long {
        val safeAttempt = nextAttempt.coerceIn(1, 16)
        val multiplier = 1L shl (safeAttempt - 1)
        return (BASE_DELAY_MS * multiplier).coerceAtMost(MAX_DELAY_MS)
    }
}
