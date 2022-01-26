package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.script.kether.runEval

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent

object TPlayerChat: TargetExtend<AsyncPlayerChatEvent>() {

    override val name = "player chat"

    override val isAsync = true

    init {
        event = AsyncPlayerChatEvent::class
        tasker{
            val questData = QuestManager.getDoingQuest(player, true)?: return@tasker player
            val message = object : ConditionType("message") {
                override fun check(): Boolean {
                    return targetTrigger(player, name, "message", message, questData)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData)
                }
            }
            TargetManager.set(name, "message", message).set(name, "number", number)
            player
        }
        TargetManager.register(name, "message").register(name, "number")
    }

    /**
     * 匹配文字
     *
     * @param name 事件名称
     * @param tag 键
     * @param content 需要的匹配内容
     */
    fun targetTrigger(player: Player, name: String, tag: String, content: String, questData: QuestData): Boolean {
        val target = (QuestManager.getDoingTarget(questData, name)?: return false).questTarget
        val condition = target.condition[tag]?: return false
        return runEval(player, "strMatch type $condition *'$content'")
    }

}