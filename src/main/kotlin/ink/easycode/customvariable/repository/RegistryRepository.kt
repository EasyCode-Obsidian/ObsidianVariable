package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope

interface RegistryRepository {

    fun upsert(entry: RegistryEntry): Boolean

    fun create(entry: RegistryEntry): Boolean

    fun update(entry: RegistryEntry): Boolean

    fun delete(scope: VariableScope, key: String): Boolean

    fun find(scope: VariableScope, key: String): RegistryEntry?

    fun list(scope: VariableScope?): List<RegistryEntry>

    fun pullUpdatedAfter(timestamp: Long): List<RegistryEntry>
}
