package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.target.util.TriggerUtils
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import org.bukkit.event.player.PlayerRespawnEvent

object TRespawn: TargetExtend<PlayerRespawnEvent>() {

    override val name = "player respawn"


    init {
        event = PlayerRespawnEvent::class
        tasker{
            player.doingTargets(name).forEach {
                val t = it.getTargetFrame()?: return@forEach
                if (TriggerUtils.booleanTrigger(player, it, t, false)) {
                    Schedule.isNumber(player, "number", it)
                }
            }
            player
        }
    }

}