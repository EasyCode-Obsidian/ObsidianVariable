package ink.easycode.customvariable.repository

import ink.easycode.customvariable.model.ServiceErrorCode

object RepositoryErrorMapper {

    inline fun <T> mapDatabaseError(action: () -> T): T {
        return try {
            action()
        } catch (ex: RepositoryException) {
            throw ex
        } catch (ex: Exception) {
            throw RepositoryException(ServiceErrorCode.DATABASE_ERROR, ex)
        }
    }
}
