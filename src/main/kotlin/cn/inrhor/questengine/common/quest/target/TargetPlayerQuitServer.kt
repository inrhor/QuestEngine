package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import java.util.*

object TargetPlayerQuitServer: TargetExtend<PlayerQuitEvent>() {

    override val name = "player quit server"

    override var priority = EventPriority.HIGHEST

    init {
        event = PlayerQuitEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player)?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val innerTarget = QuestManager.getDoingTarget(player, name)?: return@tasker player
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData, innerData, innerTarget)
                }
            }
            TargetManager.set(name, "number", number)
            player
        }
        TargetManager.register(name, "number", "number")
    }

}