package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.common.quest.target.util.Schedule
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.EventPriority
import java.util.*

object TargetPlayerQuitServer: TargetExtend<PlayerQuitEvent>() {

    override val name = "player quit server"

    override var event = PlayerQuitEvent::class

    override var priority = EventPriority.HIGHEST

    init {
        tasker{
            val questData = QuestManager.getDoingQuest(player)?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val innerTarget = QuestManager.getDoingTarget(player, name)?: return@tasker player
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return (Schedule.isNumber(player, name, "number", questData, innerData, innerTarget))
                }
            }
            TargetManager.set(name, "number", number)
            player
        }
        TargetManager.register(name, "number", "number")
    }

}