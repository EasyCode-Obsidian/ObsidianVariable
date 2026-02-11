package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.ServiceErrorCode

class RepositoryException(
    val code: ServiceErrorCode,
    cause: Throwable
) : RuntimeException(cause)
