package ink.easycode.customvariable.util

object VariableKeyValidator {

    private val keyPattern = Regex("^[a-zA-Z0-9_.:-]{1,64}$")

    fun isValid(key: String): Boolean {
        return keyPattern.matches(key)
    }
}
