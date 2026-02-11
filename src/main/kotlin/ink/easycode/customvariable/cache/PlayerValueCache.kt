package ink.easycode.customvariable.cache

import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue

class PlayerValueCache(
    ttlSeconds: Int,
    maxEntries: Int
) {

    private val ttlMillis = ttlSeconds.coerceAtLeast(1).toLong() * 1000L
    private val capacity = maxEntries.coerceAtLeast(1)
    private val lock = Any()

    private val segments = LinkedHashMap<String, PlayerCacheSegment>(16, 0.75f, true)

    fun get(ownerId: String, key: String): VariableValue? {
        val now = System.currentTimeMillis()
        synchronized(lock) {
            evictExpiredLocked(now)
            val segment = segments[ownerId] ?: return null
            segment.expireAt = expiresAt(now)
            return segment.values[key]
        }
    }

    fun listOwner(ownerId: String): List<VariableValue>? {
        val now = System.currentTimeMillis()
        synchronized(lock) {
            evictExpiredLocked(now)
            val segment = segments[ownerId] ?: return null
            segment.expireAt = expiresAt(now)
            return segment.values.values.toList()
        }
    }

    fun hasOwner(ownerId: String): Boolean {
        synchronized(lock) {
            evictExpiredLocked(System.currentTimeMillis())
            return segments.containsKey(ownerId)
        }
    }

    fun put(value: VariableValue) {
        if (value.scope != VariableScope.PLAYER) {
            return
        }
        val now = System.currentTimeMillis()
        synchronized(lock) {
            val segment = getOrCreateSegment(value.ownerId, now)
            segment.values[value.key] = value
        }
    }

    fun putAll(ownerId: String, values: List<VariableValue>) {
        val now = System.currentTimeMillis()
        val map = mutableMapOf<String, VariableValue>()
        values.forEach { value ->
            if (value.scope == VariableScope.PLAYER && value.ownerId == ownerId) {
                map[value.key] = value
            }
        }

        synchronized(lock) {
            segments[ownerId] = PlayerCacheSegment(map, expiresAt(now))
            ensureCapacityLocked()
        }
    }

    fun remove(ownerId: String, key: String) {
        synchronized(lock) {
            val segment = segments[ownerId] ?: return
            segment.values.remove(key)
            segment.expireAt = expiresAt(System.currentTimeMillis())
        }
    }

    fun invalidateOwner(ownerId: String) {
        synchronized(lock) {
            segments.remove(ownerId)
        }
    }

    fun invalidateByKey(key: String) {
        synchronized(lock) {
            segments.values.forEach { segment ->
                segment.values.remove(key)
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            segments.clear()
        }
    }

    fun evictExpired() {
        synchronized(lock) {
            evictExpiredLocked(System.currentTimeMillis())
        }
    }

    fun ownerCount(): Int {
        synchronized(lock) {
            evictExpiredLocked(System.currentTimeMillis())
            return segments.size
        }
    }

    fun ownerIds(): List<String> {
        synchronized(lock) {
            evictExpiredLocked(System.currentTimeMillis())
            return segments.keys.toList()
        }
    }

    private fun getOrCreateSegment(ownerId: String, now: Long): PlayerCacheSegment {
        val current = segments[ownerId]
        if (current != null) {
            current.expireAt = expiresAt(now)
            return current
        }

        val created = PlayerCacheSegment(mutableMapOf(), expiresAt(now))
        segments[ownerId] = created
        ensureCapacityLocked()
        return created
    }

    private fun ensureCapacityLocked() {
        while (segments.size > capacity) {
            val iterator = segments.entries.iterator()
            if (!iterator.hasNext()) {
                return
            }
            iterator.next()
            iterator.remove()
        }
    }

    private fun evictExpiredLocked(now: Long) {
        val iterator = segments.entries.iterator()
        while (iterator.hasNext()) {
            val segment = iterator.next().value
            if (segment.expireAt <= now) {
                iterator.remove()
            }
        }
    }

    private fun expiresAt(now: Long): Long {
        return now + ttlMillis
    }
}

