package ink.easycode.customvariable.apiimpl

import ink.easycode.customvariable.api.ApiVariableType
import ink.easycode.customvariable.model.VariableType

object ApiTypeMapper {

    fun toModel(type: ApiVariableType): VariableType {
        return VariableType.valueOf(type.name)
    }

    fun fromModel(type: VariableType): ApiVariableType {
        return ApiVariableType.valueOf(type.name)
    }
}
