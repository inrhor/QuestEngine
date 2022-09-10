package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.doingTargets
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
                val player = k as Player
                player.doingTargets(name).forEach {
                    val target = it.getTargetFrame()
                    if (checkName(target, mob.id)) {
                        Schedule.isNumber(player, name, "number", it)
                    }
                }
                player
            }
        }
    }

    private fun checkName(target: TargetFrame, name: String): Boolean {
        val condition = target.nodeMeta("id")?: return true
        return condition.contains(name)
    }

}