package cn.inrhor.questengine.common.quest.target.mythic

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import ink.ptms.um.event.MobDeathEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TKillMythic: TargetExtend<MobDeathEvent>() {

    override val name = "player kill mythicmobs"


    init {
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            event = MobDeathEvent::class
            tasker{
                val k = killer?: return@tasker null
                if (k !is Player ) return@tasker null
                k.triggerTarget(name) { _, pass ->
                    val mobs = pass.id
                    mobs.isEmpty() || mobs.any { it == mob.id }
                }
            }
        }
    }

}