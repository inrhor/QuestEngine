package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.quest.QuestData
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

object TPlayerDeath: TargetExtend<PlayerDeathEvent>() {

    override val name = "player death"


    init {
        event = PlayerDeathEvent::class
        tasker{
            val player = entity
            val questData = QuestManager.getDoingQuest(player, true)?: return@tasker player
            val cause = object: ConditionType(mutableListOf("cause")) {
                override fun check(): Boolean {
                    return isCause(questData, player.lastDamageCause!!.cause)
                }
            }
            val number = object: ConditionType("number") {
                override fun check(): Boolean {
                    return Schedule.isNumber(player, name, "number", questData)
                }
            }
            // 刷新
            TargetManager.set(name, "cause", cause).set(name, "number", number)
            player
        }
        // 注册
        TargetManager.register(name, "cause", mutableListOf("cause")).register(name, "number")
    }

    fun isCause(questData: QuestData, death: EntityDamageEvent.DamageCause): Boolean {
        val target = (QuestManager.getDoingTarget(questData, name)?: return false).questTarget
        val idCondition = target.conditionList["cause"]?: return false
        return idCondition.contains(death.toString())
    }

}