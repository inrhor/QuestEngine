package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

object TargetPlayerJoinServer: TargetExtend<PlayerJoinEvent>() {

    override val name = "player join server"

    init {
        event = PlayerJoinEvent::class
        tasker{
            match(player)
            player
        }
        TargetManager.register(name, "number", "number")
    }

    fun match(player: Player) {
        val questData = QuestManager.getDoingQuest(player)?: return
        if (!QuestManager.matchQuestMode(questData)) return
        val innerData = questData.questInnerData
        val targetData = QuestManager.getDoingTarget(player, name)?: return
        val innerTarget = targetData.questTarget
        val number = object: ConditionType("number") {
            override fun check(): Boolean {
                return Schedule.isNumber(player, name, "number", questData, innerData, innerTarget)
            }
        }
        TargetManager.set(name, "number", number)
    }

}