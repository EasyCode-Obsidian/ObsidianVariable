package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiErrorCode
import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.api.ApiSyncStatus
import ink.easycode.customvariable.api.VariableSyncApi
import ink.easycode.customvariable.sync.SyncManager

object SyncApiFacade : VariableSyncApi {

    override fun flushNow(): ApiResult<Unit> {
        return runSyncTask()
    }

    override fun pullNow(): ApiResult<Unit> {
        return runSyncTask()
    }

    override fun status(): ApiResult<ApiSyncStatus> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return ApiResult.Ok(ApiModelMapper.mapSync(SyncManager.status()))
    }

    private fun runSyncTask(): ApiResult<Unit> {
        if (!ApiReadyGuard.isReady()) {
            return ApiReadyGuard.notReady()
        }
        return runCatching {
            SyncManager.forceSyncNow()
            ApiResult.Ok(Unit)
        }.getOrElse { ex ->
            ApiResult.Error(
                code = ApiErrorCode.INTERNAL_ERROR,
                message = ex.message ?: "sync task failed"
            )
        }
    }
}
