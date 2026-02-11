package ink.easycode.customvariable.api

import ink.easycode.customvariable.apiimpl.CustomVariableApiBridge

object CustomVariableApi {

    fun registry(): VariableRegistryApi {
        return CustomVariableApiBridge.registry
    }

    fun access(): VariableAccessApi {
        return CustomVariableApiBridge.access
    }

    fun query(): VariableQueryApi {
        return CustomVariableApiBridge.query
    }

    fun sync(): VariableSyncApi {
        return CustomVariableApiBridge.sync
    }

    fun isReady(): Boolean {
        return CustomVariableApiBridge.isReady()
    }

    fun apiVersion(): String {
        return ApiVersion.CURRENT
    }
}
