package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiVariableScope
import ink.easycode.customvariable.model.VariableScope

object ApiScopeMapper {

    fun toModel(scope: ApiVariableScope): VariableScope {
        return when (scope) {
            ApiVariableScope.GLOBAL -> VariableScope.GLOBAL
            ApiVariableScope.PLAYER -> VariableScope.PLAYER
        }
    }

    fun fromModel(scope: VariableScope): ApiVariableScope {
        return when (scope) {
            VariableScope.GLOBAL -> ApiVariableScope.GLOBAL
            VariableScope.PLAYER -> ApiVariableScope.PLAYER
        }
    }
}
