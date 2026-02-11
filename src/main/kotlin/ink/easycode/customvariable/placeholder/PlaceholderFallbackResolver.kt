package ink.easycode.customvariable.placeholder

import ink.easycode.customvariable.config.ConfigManager

object PlaceholderFallbackResolver {

    fun resolve(): String {
        val config = ConfigManager.current().placeholder
        return when (config.missingPolicy.lowercase()) {
            "empty" -> ""
            "literal" -> config.missingLiteral
            else -> config.missingLiteral
        }
    }
}
