package ink.easycode.customvariable.api

interface VariableSyncApi {

    fun flushNow(): ApiResult<Unit>

    fun pullNow(): ApiResult<Unit>

    fun status(): ApiResult<ApiSyncStatus>
}
