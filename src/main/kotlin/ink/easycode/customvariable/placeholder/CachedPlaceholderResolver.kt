package ink.easycode.customvariable.placeholder

import ink.easycode.customvariable.cache.CacheManager

object CachedPlaceholderResolver {

    fun resolveGlobal(key: String): String? {
        return CacheManager.getGlobalValue(key)?.rawValue
    }

    fun resolvePlayer(ownerId: String, key: String): String? {
        return CacheManager.getPlayerValue(ownerId, key)?.rawValue
    }

    fun resolvePlayerOf(ownerId: String, key: String): String? {
        return resolvePlayer(ownerId, key)
    }
}
