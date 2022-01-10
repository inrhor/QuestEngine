package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.entity.Player

object Schedule {

    fun run(player: Player , name: String,
            questData: QuestData,
            questInnerData: QuestInnerData,
            targetData: TargetData, amount: Int): Boolean {
        if (targetData.schedule < amount) {
            targetData.schedule++
        }
        val allSchedule = TargetManager.scheduleUtil(name, questData, targetData)
        return RewardManager.finishReward(player, questData, questInnerData, targetData, amount, allSchedule)
    }

    fun isNumber(player: Player, name: String, meta: String, questData: QuestData, questInnerData: QuestInnerData, target: QuestTarget): Boolean {
        val idCondition = target.condition[meta]?: return false
        val targetData = questInnerData.targetsData[name]?: return false
        return run(player, name, questData, questInnerData, targetData, idCondition.toInt())
    }

}