package ink.easycode.customvariable.placeholder

import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.service.RegistryDefaultResolver
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.platform.compat.PlaceholderExpansion

object CvPlaceholderExpansion : PlaceholderExpansion {

    override val identifier: String = "cv"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        val ownerId = player?.uniqueId?.toString()
        return resolve(ownerId, args)
    }

    override fun onPlaceholderRequest(player: OfflinePlayer?, args: String): String {
        val ownerId = player?.uniqueId?.toString()
        return resolve(ownerId, args)
    }

    private fun resolve(requestOwnerId: String?, args: String): String {
        return runCatching {
            val request = args.trim()
            if (request.isEmpty()) {
                return PlaceholderFallbackResolver.resolve()
            }

            when {
                request.startsWith("global:", ignoreCase = true) -> {
                    resolveGlobal(request.substringAfter(':').trim())
                }
                request.startsWith("player:", ignoreCase = true) -> {
                    resolvePlayer(requestOwnerId, request.substringAfter(':').trim())
                }
                request.startsWith("player_of:", ignoreCase = true) -> {
                    resolvePlayerOf(request.substringAfter(':').trim())
                }
                else -> PlaceholderFallbackResolver.resolve()
            }
        }.getOrElse { ex ->
            val reason = ex.message ?: "unknown"
            warning("[CustomVariable] Placeholder parse error: args=$args, reason=$reason")
            CustomVariableLang.sendToConsole(LangKeys.PLACEHOLDER_PARSE_ERROR, args, reason)
            PlaceholderFallbackResolver.resolve()
        }
    }

    private fun resolveGlobal(key: String): String {
        if (key.isEmpty()) {
            return PlaceholderFallbackResolver.resolve()
        }

        val registry = CacheManager.getRegistry(VariableScope.GLOBAL, key)
            ?: return PlaceholderFallbackResolver.resolve()

        if (!registry.enabled) {
            return PlaceholderFallbackResolver.resolve()
        }

        val value = CacheManager.getGlobalValue(key)
        return value?.rawValue ?: RegistryDefaultResolver.resolveForRead(registry)
    }

    private fun resolvePlayer(ownerId: String?, key: String): String {
        if (ownerId.isNullOrEmpty() || key.isEmpty()) {
            return PlaceholderFallbackResolver.resolve()
        }

        val registry = CacheManager.getRegistry(VariableScope.PLAYER, key)
            ?: return PlaceholderFallbackResolver.resolve()

        if (!registry.enabled) {
            return PlaceholderFallbackResolver.resolve()
        }

        val value = CacheManager.getPlayerValue(ownerId, key)
        return value?.rawValue ?: RegistryDefaultResolver.resolveForRead(registry)
    }

    private fun resolvePlayerOf(payload: String): String {
        val splitAt = payload.indexOf(':')
        if (splitAt <= 0 || splitAt >= payload.length - 1) {
            return PlaceholderFallbackResolver.resolve()
        }

        val ownerRaw = payload.substring(0, splitAt).trim()
        val key = payload.substring(splitAt + 1).trim()
        val ownerId = PlaceholderPlayerLookup.resolveOwnerId(ownerRaw)
            ?: return PlaceholderFallbackResolver.resolve()

        return resolvePlayer(ownerId, key)
    }
}
