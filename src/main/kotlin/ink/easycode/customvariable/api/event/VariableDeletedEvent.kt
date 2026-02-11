package ink.easycode.customvariable.api.event

import ink.easycode.customvariable.api.ApiVariableScope
import taboolib.platform.type.BukkitProxyEvent

class VariableDeletedEvent(
    val scope: ApiVariableScope,
    val ownerId: String,
    val key: String
) : BukkitProxyEvent()
