package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiErrorCode
import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.bootstrap.PluginBootstrap

object ApiReadyGuard {

    fun isReady(): Boolean {
        return PluginBootstrap.isReady()
    }

    fun <T> notReady(): ApiResult<T> {
        return ApiResult.Error(
            code = ApiErrorCode.NOT_READY,
            message = "CustomVariable plugin is not ready."
        )
    }
}
