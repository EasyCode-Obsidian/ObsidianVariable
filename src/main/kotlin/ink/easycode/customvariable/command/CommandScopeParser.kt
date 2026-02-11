package ink.easycode.customvariable.command

import ink.easycode.customvariable.model.VariableScope

object CommandScopeParser {

    fun parse(raw: String): VariableScope? {
        return when (raw.lowercase()) {
            "global" -> VariableScope.GLOBAL
            "player" -> VariableScope.PLAYER
            else -> null
        }
    }
}
