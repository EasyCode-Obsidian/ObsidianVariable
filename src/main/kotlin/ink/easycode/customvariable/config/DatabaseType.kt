package ink.easycode.customvariable.config

enum class DatabaseType {
    SQLITE,
    MYSQL;

    companion object {
        fun fromValue(value: String): DatabaseType {
            return DatabaseType.entries.firstOrNull { it.name.equals(value, true) } ?: SQLITE
        }
    }
}
