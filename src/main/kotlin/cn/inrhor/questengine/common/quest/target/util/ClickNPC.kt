package cn.inrhor.questengine.common.quest.target.util

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.script.kether.evalBoolean
import org.bukkit.entity.Player

object ClickNPC {

    fun idTrigger(target: QuestTarget, id: String): Boolean {
        val idCondition = target.conditionList["id"]?: return false
        idCondition.forEach {
            if (id == it) return true
        }
        return false
    }

    fun itemTrigger(player: Player, questData: QuestData, target: QuestTarget, questInnerData: QuestInnerData): Boolean {
        val itemCondition = target.condition["item"]?: return false
        if (evalBoolean(player, "itemCheck take *$itemCondition")) {
            return RewardManager.finishReward(player, questData, questInnerData, target)
        }
        return true
    }

}