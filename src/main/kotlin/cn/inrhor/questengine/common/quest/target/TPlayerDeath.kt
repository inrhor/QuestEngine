package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

object TPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "player death"


    init {
        event = PlayerDeathEvent::class
        tasker{
            val player = entity
            QuestManager.getDoingTargets(player, name).forEach {
                if (isCause(it.questTarget, player.lastDamageCause!!.cause)) {
                    Schedule.isNumber(player, name, "number", it)
                }
            }
            player
        }
    }

    fun isCause(target: QuestTarget, death: EntityDamageEvent.DamageCause): Boolean {
        val idCondition = target.nodeMeta("cause")?: return false
        return idCondition.contains(death.toString())
    }

}