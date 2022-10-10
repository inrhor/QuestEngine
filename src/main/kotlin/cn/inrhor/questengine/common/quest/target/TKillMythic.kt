package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
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
                k.doingTargets(name).forEach {
                    val target = it.getTargetFrame()?: return@forEach
                    if (checkName(target, mob.id)) {
                        Schedule.isNumber(k, "number", it)
                    }
                }
                k
            }
        }
    }

    private fun checkName(target: TargetFrame, name: String): Boolean {
        val condition = target.nodeMeta("mobs")?: return true
        return condition.contains(name)
    }

}