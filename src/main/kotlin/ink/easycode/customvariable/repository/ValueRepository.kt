package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue

interface ValueRepository {

    fun upsert(value: VariableValue): Boolean

    fun delete(scope: VariableScope, ownerId: String, key: String): Boolean

    fun deleteByKey(scope: VariableScope, key: String): Int

    fun find(scope: VariableScope, ownerId: String, key: String): VariableValue?

    fun listByOwner(scope: VariableScope, ownerId: String): List<VariableValue>

    fun pullUpdatedAfter(timestamp: Long): List<VariableValue>

    fun resetAllByKey(scope: VariableScope, key: String, rawValue: String): Int
}
