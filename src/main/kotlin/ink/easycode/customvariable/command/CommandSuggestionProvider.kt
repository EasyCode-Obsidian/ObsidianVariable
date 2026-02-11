package ink.easycode.customvariable.command

import ink.easycode.customvariable.model.VariableType

object CommandSuggestionProvider {

    fun scopes(): List<String> {
        return listOf("global", "player")
    }

    fun types(): List<String> {
        return VariableType.entries.map { it.name.lowercase() }
    }
}
