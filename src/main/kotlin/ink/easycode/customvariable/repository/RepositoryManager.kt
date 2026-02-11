package ink.easycode.customvariable.repository

import ink.easycode.customvariable.config.PluginConfig
import javax.sql.DataSource

object RepositoryManager {

    @Volatile
    private var registryRepository: RegistryRepository? = null

    @Volatile
    private var valueRepository: ValueRepository? = null

    fun start(config: PluginConfig, dataSource: DataSource) {
        registryRepository = RepositoryFactory.createRegistryRepository(config.database.type, dataSource)
        valueRepository = RepositoryFactory.createValueRepository(config.database.type, dataSource)
    }

    fun registry(): RegistryRepository {
        return registryRepository ?: error("RegistryRepository is not initialized.")
    }

    fun value(): ValueRepository {
        return valueRepository ?: error("ValueRepository is not initialized.")
    }

    fun stop() {
        registryRepository = null
        valueRepository = null
    }
}
