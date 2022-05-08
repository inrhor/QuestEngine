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
            val questData = QuestManager.getDoingQuest(player, true)?: return@tasker player
            if (targetTrigger(player, name, "content", message, questData)) {
                Schedule.isNumber(player, name, "number", questData)
            }
            player
        }
    }

}