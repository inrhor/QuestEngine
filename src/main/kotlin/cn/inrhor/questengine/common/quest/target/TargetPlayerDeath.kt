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
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

object TargetPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "self death"

    override var event = PlayerDeathEvent::class

    init {
        tasker{
            val player = entity
            val questData = QuestManager.getDoingQuest(player)?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val innerTarget = QuestManager.getDoingTarget(player, name)?: return@tasker player
            // 建议注意顺序判断
            val cause = object: ConditionType(mutableListOf("cause")) {
                override fun check(): Boolean {
                    return (isCause(innerTarget, player.lastDamageCause!!.cause))
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return (isNumber(player, questData, innerData, innerTarget))
                }
            }
            // 刷新
            TargetManager.register(name, "cause", cause)
            TargetManager.register(name, "number", number)
            player
        }
        // 注册
        TargetManager.register(name, "cause", ConditionType(mutableListOf("cause")))
        TargetManager.register(name, "number", ConditionType("number"))
    }

    fun isCause(target: QuestTarget, death: EntityDamageEvent.DamageCause): Boolean {
        val idCondition = target.conditionList["cause"]?: return false
        return idCondition.contains(death.toString())
    }

    fun isNumber(player: Player, questData: QuestData, questInnerData: QuestInnerData, target: QuestTarget): Boolean {
        val idCondition = target.condition["number"]?: return false
        val targetData = questInnerData.targetsData[name]?: return false
        return Schedule.run(player, questData, questInnerData, target, targetData, idCondition.toInt())
    }

}