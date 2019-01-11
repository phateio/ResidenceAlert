package cf.catworlds.template.listener

import cf.catworlds.template.utils.StringTools.replaceHeart
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class EventsListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.message = e.message.replaceHeart().also {
            it.count { char -> char == '❤' }.also { count ->
                if (count != 0)
                    e.player.world.spawnParticle(Particle.HEART, e.player.eyeLocation, count * 3, 0.25, 0.25, 0.25)
            }
        }
    }

}