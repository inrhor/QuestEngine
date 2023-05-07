package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import org.bukkit.event.entity.PlayerDeathEvent

object TPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "player death"


    init {
        event = PlayerDeathEvent::class
        tasker{
            entity.triggerTarget(name) { _, pass ->
                val cause = pass.cause
                cause.isEmpty() ||
                        cause.any { it == entity.lastDamageCause?.cause?.name }
            }
        }
    }

}