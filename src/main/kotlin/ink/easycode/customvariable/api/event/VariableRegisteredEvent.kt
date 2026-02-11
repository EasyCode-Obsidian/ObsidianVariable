package ink.easycode.customvariable.api.event

import ink.easycode.customvariable.api.ApiRegistryEntry
import taboolib.platform.type.BukkitProxyEvent

class VariableRegisteredEvent(
    val entry: ApiRegistryEntry
) : BukkitProxyEvent()
