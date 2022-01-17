package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.Bukkit

object TNpcRight: TargetExtend<NPCRightClickEvent>() {

    override val name = "right npc"

    init {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            event = NPCRightClickEvent::class
            tasker {
                val player = clicker
                TNpcLeft.match(player, npc.id.toString(), name)
                player
            }
            // 注册
            TargetManager
                .register(name, "id", mutableListOf("id"))
                .register(name, "need", mutableListOf("need"))
        }
    }



}