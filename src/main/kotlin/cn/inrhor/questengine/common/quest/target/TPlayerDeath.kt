package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils
import cn.inrhor.questengine.common.database.data.doingTargets
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

object TPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "player death"


    init {
        event = PlayerDeathEvent::class
        tasker{
            val player = entity
            player.doingTargets(name).forEach {
                if (isCause(it.getTargetFrame(), player.lastDamageCause!!.cause)) {
                    if (TriggerUtils.booleanTrigger(player, it, it.getTargetFrame(), false)) {
                        Schedule.isNumber(player, name, "number", it)
                    }
                }
            }
            player
        }
    }

    fun isCause(target: TargetFrame, death: EntityDamageEvent.DamageCause): Boolean {
        val idCondition = target.nodeMeta("cause")?: return true
        return idCondition.contains(death.toString())
    }

}