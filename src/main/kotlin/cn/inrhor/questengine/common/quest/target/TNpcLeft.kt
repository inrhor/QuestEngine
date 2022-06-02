package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils
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
        QuestManager.getDoingTargets(player, name).forEach {
            if (TriggerUtils.idTrigger(it.questTarget, npcID)) {
                TriggerUtils.booleanTrigger(player, it.questTarget)
            }
        }
    }

}