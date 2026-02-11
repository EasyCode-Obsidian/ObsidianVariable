package ink.easycode.customvariable.repository

class SchemaMigrationException(
    message: String,
    cause: Throwable
) : RuntimeException(message, cause)
