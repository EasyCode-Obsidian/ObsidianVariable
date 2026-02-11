package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope

class SafeRegistryRepository(
    private val delegate: RegistryRepository
) : RegistryRepository {

    override fun upsert(entry: RegistryEntry): Boolean {
        return RepositoryErrorMapper.mapDatabaseError { delegate.upsert(entry) }
    }

    override fun create(entry: RegistryEntry): Boolean {
        return RepositoryErrorMapper.mapDatabaseError { delegate.create(entry) }
    }

    override fun update(entry: RegistryEntry): Boolean {
        return RepositoryErrorMapper.mapDatabaseError { delegate.update(entry) }
    }

    override fun delete(scope: VariableScope, key: String): Boolean {
        return RepositoryErrorMapper.mapDatabaseError { delegate.delete(scope, key) }
    }

    override fun find(scope: VariableScope, key: String): RegistryEntry? {
        return RepositoryErrorMapper.mapDatabaseError { delegate.find(scope, key) }
    }

    override fun list(scope: VariableScope?): List<RegistryEntry> {
        return RepositoryErrorMapper.mapDatabaseError { delegate.list(scope) }
    }

    override fun pullUpdatedAfter(timestamp: Long): List<RegistryEntry> {
        return RepositoryErrorMapper.mapDatabaseError { delegate.pullUpdatedAfter(timestamp) }
    }
}
