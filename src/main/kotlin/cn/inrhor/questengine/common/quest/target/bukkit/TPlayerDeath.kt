package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

object TPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "player death"


    init {
        event = PlayerDeathEvent::class
        tasker{
            entity.triggerTarget(name) { _, pass ->
                val cause = pass.cause
                cause.isEmpty() ||
                        cause.any { cause.contains(entity.lastDamageCause.toString()) }
            }

            /*player.doingTargets(name).forEach {
                val t = it.getTargetFrame()?: return@forEach
                if (isCause(t, player.lastDamageCause!!.cause)) {
                    if (TriggerUtils.booleanTrigger(player, it, t, false)) {
                        Schedule.isNumber(player, "number", it)
                    }
                }
            }
            player*/
        }
    }

    fun isCause(target: TargetFrame, death: EntityDamageEvent.DamageCause): Boolean {
        val idCondition = target.nodeMeta("cause")?: return true
        return idCondition.contains(death.toString())
    }

}