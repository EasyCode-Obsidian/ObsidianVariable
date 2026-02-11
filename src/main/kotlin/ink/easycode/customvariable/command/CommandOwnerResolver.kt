package ink.easycode.customvariable.command

import ink.easycode.customvariable.model.ServiceErrorCode
import ink.easycode.customvariable.model.ServiceResult
import taboolib.common.platform.function.getProxyPlayer
import java.util.UUID

object CommandOwnerResolver {

    fun resolve(raw: String): ServiceResult<String> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            return ServiceResult.Error(ServiceErrorCode.PLAYER_NOT_FOUND, arrayOf(raw))
        }

        val uuid = runCatching { UUID.fromString(trimmed) }.getOrNull()
        if (uuid != null) {
            return ServiceResult.Ok(uuid.toString())
        }

        val player = getProxyPlayer(trimmed)
            ?: return ServiceResult.Error(ServiceErrorCode.PLAYER_NOT_FOUND, arrayOf(trimmed))

        return ServiceResult.Ok(player.uniqueId.toString())
    }
}
