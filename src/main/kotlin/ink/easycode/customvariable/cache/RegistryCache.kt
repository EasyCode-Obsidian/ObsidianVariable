package ink.easycode.customvariable.cache

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.RegistryKey
import ink.easycode.customvariable.model.VariableScope
import java.util.concurrent.ConcurrentHashMap

class RegistryCache {

    private val entries = ConcurrentHashMap<RegistryKey, RegistryEntry>()

    fun rebuild(values: List<RegistryEntry>) {
        entries.clear()
        values.forEach(::put)
    }

    fun put(entry: RegistryEntry) {
        entries[RegistryKey(entry.scope, entry.key)] = entry
    }

    fun get(scope: VariableScope, key: String): RegistryEntry? {
        return entries[RegistryKey(scope, key)]
    }

    fun remove(scope: VariableScope, key: String) {
        entries.remove(RegistryKey(scope, key))
    }

    fun invalidateByKey(key: String) {
        val removing = entries.keys.filter { it.key == key }
        removing.forEach(entries::remove)
    }

    fun list(scope: VariableScope?): List<RegistryEntry> {
        if (scope == null) {
            return entries.values.toList()
        }
        return entries.values.filter { it.scope == scope }
    }

    fun clear() {
        entries.clear()
    }

    fun size(): Int {
        return entries.size
    }
}
