package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.script.kether.evalBoolean
import org.bukkit.entity.Player

object TriggerUtils {

    fun idTrigger(target: QuestTarget, id: String): Boolean {
        val idCondition = target.conditionList["id"]?: return false
        idCondition.forEach {
            if (id == it) return true
        }
        return false
    }

    fun booleanTrigger(player: Player, questData: QuestData, targetData: TargetData, questInnerData: QuestInnerData): Boolean {
        val needCondition = targetData.questTarget.conditionList["need"]?: return false
        if (evalBoolean(player, needCondition)) {
            return RewardManager.finishReward(player, questData, questInnerData, targetData)
        }
        return true
    }

}