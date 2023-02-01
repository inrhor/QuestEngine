package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.target.TJoinCsg.matchCsg
import customgo.event.PlayerLeaveLobbyEvent
import org.bukkit.Bukkit

object TQuitCsg: TargetExtend<PlayerLeaveLobbyEvent>() {

    override val name = "quit csg"

    init {
        if (Bukkit.getPluginManager().getPlugin("Csg-Plus") != null) {
            event = PlayerLeaveLobbyEvent::class
            tasker {
                val player = player
                matchCsg(player, lobby.name, name)
                player
            }
        }
    }

}