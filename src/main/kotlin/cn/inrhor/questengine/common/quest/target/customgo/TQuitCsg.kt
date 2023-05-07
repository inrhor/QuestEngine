package cn.inrhor.questengine.common.quest.target.customgo

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import customgo.event.PlayerLeaveLobbyEvent
import org.bukkit.Bukkit

object TQuitCsg: TargetExtend<PlayerLeaveLobbyEvent>() {

    override val name = "quit csg"

    init {
        if (Bukkit.getPluginManager().getPlugin("Csg-Plus") != null) {
            event = PlayerLeaveLobbyEvent::class
            tasker {
                player.triggerTarget(TJoinCsg.name) { _, pass ->
                    val id = pass.id
                    id.isEmpty() || id.any { it == lobby.name }
                }
            }
        }
    }

}