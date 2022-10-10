package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import net.citizensnpcs.api.event.NPCLeftClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TNpcLeft: TargetExtend<NPCLeftClickEvent>() {

    override val name = "left npc"

    init {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            event = NPCLeftClickEvent::class
            tasker {
                val player = clicker
                match(player, npc.id.toString(), name)
                player
            }
        }
    }

    fun match(player: Player, npcID: String, name: String) {
        player.doingTargets(name).forEach {
            val target = it.getTargetFrame()?: return@forEach
            if (TriggerUtils.idTrigger(target, npcID)) {
                TriggerUtils.booleanTrigger(player, it, target)
            }
        }
    }

}