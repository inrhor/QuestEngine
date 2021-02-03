package cn.inrhor.questengine.common.listener.holo

import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent

@TListener
class ClickHolo: Listener {

    @EventHandler
    fun clickAction(ev: PlayerAnimationEvent) {

    }

}