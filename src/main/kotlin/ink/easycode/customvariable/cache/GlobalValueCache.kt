package ink.easycode.customvariable.cache

import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue
import java.util.concurrent.ConcurrentHashMap

class GlobalValueCache {

    private val values = ConcurrentHashMap<String, VariableValue>()

    fun rebuild(globalValues: List<VariableValue>) {
        values.clear()
        globalValues.filter { it.scope == VariableScope.GLOBAL }.forEach(::put)
    }

    fun put(value: VariableValue) {
        if (value.scope != VariableScope.GLOBAL) {
            return
        }
        values[value.key] = value
    }

    fun get(key: String): VariableValue? {
        return values[key]
    }

    fun list(): List<VariableValue> {
        return values.values.toList()
    }

    fun remove(key: String) {
        values.remove(key)
    }

    fun invalidateByKey(key: String) {
        values.remove(key)
    }

    fun clear() {
        values.clear()
    }

    fun size(): Int {
        return values.size
    }
}
