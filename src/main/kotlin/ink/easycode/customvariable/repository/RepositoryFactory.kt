package ink.easycode.customvariable.repository

import ink.easycode.customvariable.config.DatabaseType
import javax.sql.DataSource

object RepositoryFactory {

    fun createRegistryRepository(
        type: DatabaseType,
        dataSource: DataSource
    ): RegistryRepository {
        val delegate = if (type == DatabaseType.SQLITE) {
            SqliteRegistryRepository(dataSource)
        } else {
            MysqlRegistryRepository(dataSource)
        }
        return SafeRegistryRepository(delegate)
    }

    fun createValueRepository(
        type: DatabaseType,
        dataSource: DataSource
    ): ValueRepository {
        val delegate = if (type == DatabaseType.SQLITE) {
            SqliteValueRepository(dataSource)
        } else {
            MysqlValueRepository(dataSource)
        }
        return SafeValueRepository(delegate)
    }
}
