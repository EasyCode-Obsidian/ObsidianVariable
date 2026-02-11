package ink.easycode.customvariable.sync

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.RegistryKey
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import java.util.concurrent.ConcurrentHashMap

class SyncQueueStore {

    private val registryQueue = ConcurrentHashMap<RegistryKey, RegistryQueueEntry>()
    private val valueQueue = ConcurrentHashMap<ValueQueueKey, ValueQueueEntry>()

    fun enqueueRegistryUpsert(entry: RegistryEntry) {
        val key = RegistryKey(entry.scope, entry.key)
        registryQueue[key] = RegistryQueueEntry(
            key = key,
            action = RegistryWriteAction.UPSERT,
            entry = entry,
            attempts = 0,
            nextRetryAt = 0L,
            version = entry.version,
            updatedAt = entry.updatedAt
        )
    }

    fun enqueueRegistryDelete(scope: VariableScope, key: String, version: Long, updatedAt: Long) {
        val queueKey = RegistryKey(scope, key)
        registryQueue[queueKey] = RegistryQueueEntry(
            key = queueKey,
            action = RegistryWriteAction.DELETE,
            entry = null,
            attempts = 0,
            nextRetryAt = 0L,
            version = version,
            updatedAt = updatedAt
        )
    }

    fun enqueueValueUpsert(value: VariableValue) {
        val key = ValueQueueKey(value.scope, value.ownerId, value.key)
        valueQueue[key] = ValueQueueEntry(
            key = key,
            action = ValueWriteAction.UPSERT,
            value = value,
            attempts = 0,
            nextRetryAt = 0L,
            version = value.version,
            updatedAt = value.updatedAt
        )
    }

    fun enqueueValueDelete(
        scope: VariableScope,
        ownerId: String,
        key: String,
        version: Long,
        updatedAt: Long
    ) {
        val queueKey = ValueQueueKey(scope, ownerId, key)
        valueQueue[queueKey] = ValueQueueEntry(
            key = queueKey,
            action = ValueWriteAction.DELETE,
            value = null,
            attempts = 0,
            nextRetryAt = 0L,
            version = version,
            updatedAt = updatedAt
        )
    }

    fun pollRegistryDue(limit: Int, now: Long): List<RegistryQueueEntry> {
        return registryQueue.values
            .asSequence()
            .filter { it.nextRetryAt <= now }
            .sortedWith(compareBy<RegistryQueueEntry> { it.updatedAt }.thenBy { it.key.key })
            .take(limit.coerceAtLeast(1))
            .toList()
    }

    fun pollValueDue(limit: Int, now: Long): List<ValueQueueEntry> {
        return valueQueue.values
            .asSequence()
            .filter { it.nextRetryAt <= now }
            .sortedWith(compareBy<ValueQueueEntry> { it.updatedAt }.thenBy { it.key.key })
            .take(limit.coerceAtLeast(1))
            .toList()
    }

    fun markRegistrySuccess(entry: RegistryQueueEntry) {
        registryQueue.remove(entry.key, entry)
    }

    fun markValueSuccess(entry: ValueQueueEntry) {
        valueQueue.remove(entry.key, entry)
    }

    fun markRegistryRetry(entry: RegistryQueueEntry, nextRetryAt: Long) {
        updateRegistryIfSame(entry) {
            entry.copy(attempts = entry.attempts + 1, nextRetryAt = nextRetryAt)
        }
    }

    fun markValueRetry(entry: ValueQueueEntry, nextRetryAt: Long) {
        updateValueIfSame(entry) {
            entry.copy(attempts = entry.attempts + 1, nextRetryAt = nextRetryAt)
        }
    }

    fun unlockRetries() {
        registryQueue.replaceAll { _, entry -> entry.copy(nextRetryAt = 0L) }
        valueQueue.replaceAll { _, entry -> entry.copy(nextRetryAt = 0L) }
    }

    fun registrySize(): Int {
        return registryQueue.size
    }

    fun valueSize(): Int {
        return valueQueue.size
    }

    fun hasPending(): Boolean {
        return registryQueue.isNotEmpty() || valueQueue.isNotEmpty()
    }

    fun clear() {
        registryQueue.clear()
        valueQueue.clear()
    }

    private fun updateRegistryIfSame(entry: RegistryQueueEntry, updater: () -> RegistryQueueEntry) {
        val current = registryQueue[entry.key]
        if (current == entry) {
            registryQueue[entry.key] = updater()
        }
    }

    private fun updateValueIfSame(entry: ValueQueueEntry, updater: () -> ValueQueueEntry) {
        val current = valueQueue[entry.key]
        if (current == entry) {
            valueQueue[entry.key] = updater()
        }
    }
}
