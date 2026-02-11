package ink.easycode.customvariable.apiimpl.event

import ink.easycode.customvariable.api.event.VariableDeletedEvent
import ink.easycode.customvariable.api.event.VariableRegisteredEvent
import ink.easycode.customvariable.api.event.VariableTypeChangedEvent
import ink.easycode.customvariable.api.event.VariableValueChangedEvent
import ink.easycode.customvariable.apiimpl.ApiModelMapper
import ink.easycode.customvariable.apiimpl.ApiScopeMapper
import ink.easycode.customvariable.apiimpl.ApiTypeMapper
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.model.VariableType
import ink.easycode.customvariable.model.VariableValue
import taboolib.common.platform.function.warning
import taboolib.platform.type.BukkitProxyEvent

object ApiEventPublisher {

    fun publishRegistered(entry: RegistryEntry) {
        call(VariableRegisteredEvent(ApiModelMapper.mapRegistry(entry)))
    }

    fun publishTypeChanged(
        scope: VariableScope,
        key: String,
        oldType: VariableType,
        newType: VariableType,
        resetValueRaw: String
    ) {
        call(
            VariableTypeChangedEvent(
                scope = ApiScopeMapper.fromModel(scope),
                key = key,
                oldType = ApiTypeMapper.fromModel(oldType),
                newType = ApiTypeMapper.fromModel(newType),
                resetValueRaw = resetValueRaw
            )
        )
    }

    fun publishValueChanged(value: VariableValue) {
        call(VariableValueChangedEvent(ApiModelMapper.mapValue(value)))
    }

    fun publishDeleted(scope: VariableScope, ownerId: String, key: String) {
        call(
            VariableDeletedEvent(
                scope = ApiScopeMapper.fromModel(scope),
                ownerId = ownerId,
                key = key
            )
        )
    }

    private fun call(event: BukkitProxyEvent) {
        runCatching { event.call() }.onFailure { ex ->
            warning("[CustomVariable] API event dispatch failed: ${event.javaClass.simpleName}, reason=${ex.message}")
        }
    }
}
