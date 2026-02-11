package ink.easycode.customvariable.command

import ink.easycode.customvariable.model.VariableType

object CommandTypeParser {

    fun parse(raw: String): VariableType? {
        return VariableType.entries.firstOrNull { it.name.equals(raw, true) }
    }
}
