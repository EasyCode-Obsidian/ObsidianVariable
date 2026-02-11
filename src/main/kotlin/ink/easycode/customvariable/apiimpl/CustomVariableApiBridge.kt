package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.VariableAccessApi
import ink.easycode.customvariable.api.VariableQueryApi
import ink.easycode.customvariable.api.VariableRegistryApi
import ink.easycode.customvariable.api.VariableSyncApi

object CustomVariableApiBridge {

    val registry: VariableRegistryApi = RegistryApiFacade
    val access: VariableAccessApi = AccessApiFacade
    val query: VariableQueryApi = QueryApiFacade
    val sync: VariableSyncApi = SyncApiFacade

    fun isReady(): Boolean {
        return ApiReadyGuard.isReady()
    }
}
