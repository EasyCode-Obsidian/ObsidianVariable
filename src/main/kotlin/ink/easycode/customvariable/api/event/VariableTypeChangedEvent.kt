package ink.easycode.customvariable.api.event

import ink.easycode.customvariable.api.ApiVariableScope
import ink.easycode.customvariable.api.ApiVariableType
import taboolib.platform.type.BukkitProxyEvent

class VariableTypeChangedEvent(
    val scope: ApiVariableScope,
    val key: String,
    val oldType: ApiVariableType,
    val newType: ApiVariableType,
    val resetValueRaw: String
) : BukkitProxyEvent()
