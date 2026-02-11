package ink.easycode.customvariable.service

import ink.easycode.customvariable.model.ServiceErrorCode
import ink.easycode.customvariable.model.ServiceResult
import ink.easycode.customvariable.repository.RepositoryException

object ServiceErrorMapper {

    inline fun <T> map(block: () -> ServiceResult<T>): ServiceResult<T> {
        return try {
            block()
        } catch (ex: RepositoryException) {
            ServiceResult.Error(ex.code)
        } catch (_: IllegalArgumentException) {
            ServiceResult.Error(ServiceErrorCode.INVALID_TYPE_VALUE)
        } catch (_: Exception) {
            ServiceResult.Error(ServiceErrorCode.INTERNAL_ERROR)
        }
    }
}
