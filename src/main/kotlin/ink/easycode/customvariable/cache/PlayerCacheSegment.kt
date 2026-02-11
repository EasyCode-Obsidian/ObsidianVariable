package ink.easycode.customvariable.cache

import ink.easycode.customvariable.model.VariableValue

data class PlayerCacheSegment(
    val values: MutableMap<String, VariableValue>,
    var expireAt: Long
)
