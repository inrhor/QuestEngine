package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.script.kether.evalBoolean
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

object TargetPlayerChat: TargetExtend<AsyncPlayerChatEvent>() {

    override val name = "player chat"

    override val isAsync = true

    init {
        event = AsyncPlayerChatEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player)?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val targetData = QuestManager.getDoingTarget(player, name)?: return@tasker player
            val innerTarget = targetData.questTarget
            val message = object : ConditionType("message") {
                override fun check(): Boolean {
                    return targetTrigger(player, message, innerTarget)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData, innerData, innerTarget)
                }
            }
            TargetManager.set(name, "message", message)
            TargetManager.set(name, "number", number)
            player
        }
        TargetManager.register(name, "message", "message")
        TargetManager.register(name, "number", "number")
    }

    fun targetTrigger(player: Player, msg: String, target: QuestTarget): Boolean {
        val condition = target.condition["message"]?: return false
        return evalBoolean(player, "strMatch type $condition *'$msg'")
    }

}