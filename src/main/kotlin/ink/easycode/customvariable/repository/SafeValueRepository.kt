package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableValue

class SafeValueRepository(
    private val delegate: ValueRepository
) : ValueRepository {

    override fun upsert(value: VariableValue): Boolean {
        return RepositoryErrorMapper.mapDatabaseError { delegate.upsert(value) }
    }

    override fun delete(scope: VariableScope, ownerId: String, key: String): Boolean {
        return RepositoryErrorMapper.mapDatabaseError { delegate.delete(scope, ownerId, key) }
    }

    override fun deleteByKey(scope: VariableScope, key: String): Int {
        return RepositoryErrorMapper.mapDatabaseError { delegate.deleteByKey(scope, key) }
    }

    override fun find(scope: VariableScope, ownerId: String, key: String): VariableValue? {
        return RepositoryErrorMapper.mapDatabaseError { delegate.find(scope, ownerId, key) }
    }

    override fun listByOwner(scope: VariableScope, ownerId: String): List<VariableValue> {
        return RepositoryErrorMapper.mapDatabaseError { delegate.listByOwner(scope, ownerId) }
    }

    override fun pullUpdatedAfter(timestamp: Long): List<VariableValue> {
        return RepositoryErrorMapper.mapDatabaseError { delegate.pullUpdatedAfter(timestamp) }
    }

    override fun resetAllByKey(scope: VariableScope, key: String, rawValue: String): Int {
        return RepositoryErrorMapper.mapDatabaseError { delegate.resetAllByKey(scope, key, rawValue) }
    }
}
