package ink.easycode.customvariable.sync

import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.repository.RegistryRepository
import ink.easycode.customvariable.repository.ValueRepository
import taboolib.common.platform.function.warning

object SyncFlushExecutor {

    fun flushOnce(
        queueStore: SyncQueueStore,
        registryRepository: RegistryRepository,
        valueRepository: ValueRepository,
        batchSize: Int
    ): Boolean {
        val safeBatchSize = batchSize.coerceAtLeast(1)
        val now = System.currentTimeMillis()
        var failed = 0
        failed += flushRegistryBatch(queueStore, registryRepository, valueRepository, safeBatchSize, now)
        failed += flushValueBatch(queueStore, valueRepository, safeBatchSize, now)
        return failed == 0
    }

    fun flushAllPending(
        queueStore: SyncQueueStore,
        registryRepository: RegistryRepository,
        valueRepository: ValueRepository,
        maxRounds: Int = 20
    ) {
        var round = 0
        while (queueStore.hasPending() && round < maxRounds) {
            queueStore.unlockRetries()
            flushOnce(queueStore, registryRepository, valueRepository, Int.MAX_VALUE)
            round += 1
        }
        if (queueStore.hasPending()) {
            val reason = "force flush reached max rounds: $maxRounds"
            SyncMetrics.recordFailure("flush:$reason")
            warning("[CustomVariable] $reason")
            CustomVariableLang.sendToConsole(LangKeys.SYNC_FLUSH_FAILED, "shutdown", maxRounds, reason)
        }
    }

    private fun flushRegistryBatch(
        queueStore: SyncQueueStore,
        registryRepository: RegistryRepository,
        valueRepository: ValueRepository,
        batchSize: Int,
        now: Long
    ): Int {
        val batch = queueStore.pollRegistryDue(batchSize, now)
        var failed = 0

        batch.forEach { entry ->
            val success = runCatching {
                applyRegistryWrite(registryRepository, valueRepository, entry)
                true
            }.getOrElse { ex ->
                failed += 1
                onFlushFailure("registry:${entry.key.scope}:${entry.key.key}", entry.attempts + 1, ex)
                queueStore.markRegistryRetry(entry, now + SyncRetryPolicy.nextDelayMillis(entry.attempts + 1))
                false
            }
            if (success) {
                queueStore.markRegistrySuccess(entry)
            }
        }
        return failed
    }

    private fun flushValueBatch(
        queueStore: SyncQueueStore,
        valueRepository: ValueRepository,
        batchSize: Int,
        now: Long
    ): Int {
        val batch = queueStore.pollValueDue(batchSize, now)
        var failed = 0

        batch.forEach { entry ->
            val success = runCatching {
                applyValueWrite(valueRepository, entry)
                true
            }.getOrElse { ex ->
                failed += 1
                onFlushFailure(
                    "value:${entry.key.scope}:${entry.key.ownerId}:${entry.key.key}",
                    entry.attempts + 1,
                    ex
                )
                queueStore.markValueRetry(entry, now + SyncRetryPolicy.nextDelayMillis(entry.attempts + 1))
                false
            }
            if (success) {
                queueStore.markValueSuccess(entry)
            }
        }
        return failed
    }

    private fun applyRegistryWrite(
        registryRepository: RegistryRepository,
        valueRepository: ValueRepository,
        entry: RegistryQueueEntry
    ) {
        when (entry.action) {
            RegistryWriteAction.UPSERT -> registryRepository.upsert(entry.entry ?: return)
            RegistryWriteAction.DELETE -> {
                registryRepository.delete(entry.key.scope, entry.key.key)
                valueRepository.deleteByKey(entry.key.scope, entry.key.key)
            }
        }
    }

    private fun applyValueWrite(repository: ValueRepository, entry: ValueQueueEntry) {
        when (entry.action) {
            ValueWriteAction.UPSERT -> repository.upsert(entry.value ?: return)
            ValueWriteAction.DELETE -> repository.delete(entry.key.scope, entry.key.ownerId, entry.key.key)
        }
    }

    private fun onFlushFailure(target: String, attempt: Int, ex: Throwable) {
        val reason = ex.message ?: ex::class.java.simpleName
        SyncMetrics.recordFailure("flush:$target:$reason")
        warning("[CustomVariable] Flush failed ($target) attempt=$attempt: $reason")
        CustomVariableLang.sendToConsole(LangKeys.SYNC_FLUSH_FAILED, target, attempt, reason)
    }
}
