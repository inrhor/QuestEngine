package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils
import customgo.event.PlayerJoinLobbyEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TJoinCsg: TargetExtend<PlayerJoinLobbyEvent>() {

    override val name = "join csg"

    init {
        if (Bukkit.getPluginManager().getPlugin("Csg-Plus") != null) {
            event = PlayerJoinLobbyEvent::class
            tasker {
                val player = player
                matchCsg(player, lobby.name, name)
                player
            }
        }
    }

    fun matchCsg(player: Player, id: String, name: String) {
        player.doingTargets(name).forEach {
            val target = it.getTargetFrame()?: return@forEach
            if (TriggerUtils.idTrigger(target, id, "room")) {
                TriggerUtils.booleanTrigger(player, it, target)
            }
        }
    }

}