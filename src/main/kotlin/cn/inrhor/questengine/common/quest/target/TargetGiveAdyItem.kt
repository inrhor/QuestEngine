package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.script.kether.KetherHandler
import org.bukkit.entity.Player
import java.util.*

object TargetGiveAdyItem/*: TargetExtend<AdyeshachEntityInteractEvent>()*/ {

    /*override val name = "give ady item"

    override var event = AdyeshachEntityInteractEvent::class

    init {
        tasker{
            val questData = QuestManager.getDoingQuest(player)?: return@tasker player
            if (!QuestManager.matchQuestMode(questData)) return@tasker player
            val innerData = questData.questInnerData
            val innerTarget = QuestManager.getDoingTarget(player, name)?: return@tasker player
            // 建议注意顺序判断
            val id = object: ConditionType("id") {
                override fun check(): Boolean {
                    return (idTrigger(innerTarget, entity.id))
                }
            }
            val item = object: ConditionType("item") {
                override fun check(): Boolean {
                    return (itemTrigger(player, questData, innerTarget, innerData))
                }
            }
            // 刷新
            TargetManager.register(name, id)
            TargetManager.register(name, item)
            player
        }
        // 注册
        TargetManager.register(name, ConditionType(mutableListOf("id")))
        TargetManager.register(name, ConditionType("item"))
    }

    fun idTrigger(target: QuestTarget, id: String): Boolean {
        val idCondition = target.conditionList["id"]?: return false
        idCondition.forEach {
            if (id == it) return true
        }
        return false
    }

    fun itemTrigger(player: Player, questData: QuestData, target: QuestTarget, questInnerData: QuestInnerData): Boolean {
        val itemCondition = target.condition["item"]?: return false
        if (KetherHandler.evalBoolean(player, "itemCheck take *$itemCondition")) {
            return RewardManager.finishReward(player, questData, questInnerData, target)
        }
        return true
    }*/

}