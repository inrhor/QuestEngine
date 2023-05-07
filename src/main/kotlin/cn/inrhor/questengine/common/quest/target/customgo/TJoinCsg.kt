package cn.inrhor.questengine.common.quest.target.customgo

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import customgo.event.PlayerJoinLobbyEvent
import org.bukkit.Bukkit

object TJoinCsg: TargetExtend<PlayerJoinLobbyEvent>() {

    override val name = "join csg"

    init {
        if (Bukkit.getPluginManager().getPlugin("Csg-Plus") != null) {
            event = PlayerJoinLobbyEvent::class
            tasker {
                player.triggerTarget(name) { _, pass ->
                    val id = pass.id
                    id.isEmpty() || id.any { it == lobby.name }
                }
            }
        }
    }

}