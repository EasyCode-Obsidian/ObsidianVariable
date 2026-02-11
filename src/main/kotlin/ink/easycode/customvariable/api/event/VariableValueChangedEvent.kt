package ink.easycode.customvariable.api.event

import ink.easycode.customvariable.api.ApiVariableValue
import taboolib.platform.type.BukkitProxyEvent

class VariableValueChangedEvent(
    val value: ApiVariableValue
) : BukkitProxyEvent()
