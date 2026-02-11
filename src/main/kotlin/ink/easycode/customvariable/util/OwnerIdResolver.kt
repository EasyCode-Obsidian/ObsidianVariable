package ink.easycode.customvariable.util

import ink.easycode.customvariable.model.VariableScope

object OwnerIdResolver {

    private const val GLOBAL_OWNER_ID = "_global_"

    fun globalOwnerId(): String {
        return GLOBAL_OWNER_ID
    }

    fun normalize(scope: VariableScope, ownerId: String?): String {
        return if (scope == VariableScope.GLOBAL) {
            GLOBAL_OWNER_ID
        } else {
            ownerId?.trim().orEmpty()
        }
    }
}
