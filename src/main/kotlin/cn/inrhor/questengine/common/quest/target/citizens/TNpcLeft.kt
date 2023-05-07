package cn.inrhor.questengine.common.quest.target.citizens

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import cn.inrhor.questengine.script.kether.runEval
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
            }
        }
    }

    fun match(player: Player, npcID: String, name: String): Player {
        return player.triggerTarget(name) { _, pass ->
            val id = pass.id
            val need = pass.need
            (id.isEmpty() || id.any { it == npcID }) && (need.isEmpty() || runEval(player, need))
        }
    }

}