package ink.easycode.customvariable.cache

import ink.easycode.customvariable.service.RegistryValueSeeder
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

object PlayerCacheListener {

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val ownerId = event.player.uniqueId.toString()
        submitAsync {
            CacheManager.warmPlayer(ownerId)
            RegistryValueSeeder.ensurePlayerDefaults(ownerId)
        }
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val ownerId = event.player.uniqueId.toString()
        CacheManager.invalidatePlayer(ownerId)
    }
}
