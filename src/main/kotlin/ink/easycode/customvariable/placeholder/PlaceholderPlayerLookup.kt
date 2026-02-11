package ink.easycode.customvariable.placeholder

import taboolib.common.platform.function.getProxyPlayer
import java.util.UUID

object PlaceholderPlayerLookup {

    fun resolveOwnerId(raw: String): String? {
        val text = raw.trim()
        if (text.isEmpty()) {
            return null
        }

        val uuid = runCatching { UUID.fromString(text) }.getOrNull()
        if (uuid != null) {
            return uuid.toString()
        }

        return getProxyPlayer(text)?.uniqueId?.toString()
    }
}
