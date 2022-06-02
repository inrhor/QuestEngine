package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.quest.target.TPlayerChat.targetTrigger
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object TPlayerCommand: TargetExtend<PlayerCommandPreprocessEvent>() {

    override val name = "player send command"

    init {
        event = PlayerCommandPreprocessEvent::class
        tasker{
            QuestManager.getDoingTargets(player, name).forEach {
                if (targetTrigger(player, name, "content", message, it.questTarget)) {
                    Schedule.isNumber(player, name, "number", it)
                }
            }
            player
        }
    }

}