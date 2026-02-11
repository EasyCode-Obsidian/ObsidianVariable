package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableType
import ink.easycode.customvariable.model.VariableValue
import java.sql.ResultSet

object RepositoryMapper {

    fun mapRegistry(resultSet: ResultSet): RegistryEntry {
        return RegistryEntry(
            scope = VariableScope.valueOf(resultSet.getString("scope")),
            key = resultSet.getString("var_key"),
            type = VariableType.valueOf(resultSet.getString("value_type")),
            defaultRaw = resultSet.getString("default_raw"),
            description = resultSet.getString("description"),
            enabled = resultSet.getInt("enabled") == 1,
            version = resultSet.getLong("version"),
            updatedAt = resultSet.getLong("updated_at")
        )
    }

    fun mapValue(resultSet: ResultSet): VariableValue {
        return VariableValue(
            scope = VariableScope.valueOf(resultSet.getString("scope")),
            ownerId = resultSet.getString("owner_id"),
            key = resultSet.getString("var_key"),
            rawValue = resultSet.getString("raw_value"),
            version = resultSet.getLong("version"),
            updatedAt = resultSet.getLong("updated_at")
        )
    }
}
