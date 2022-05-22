package cn.inrhor.questengine.api.target.util

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.entity.Player

object Schedule {

    fun run(player: Player , name: String,
            questData: QuestData,
            targetData: TargetData, amount: Int): Boolean {
        if (targetData.schedule < amount) {
            targetData.schedule++
        }
        val allSchedule = TargetManager.scheduleUtil(name, questData, targetData)
        return RewardManager.finishReward(player, questData, questData.questInnerData, targetData, amount, allSchedule)
    }

    fun isNumber(player: Player, name: String, meta: String, questData: QuestData): Boolean {
        val target = (QuestManager.getDoingTarget(questData, name)?: return false).questTarget
        val con = target.nodeMeta(meta)?: return false
        val questInnerData = questData.questInnerData
        val targetData = questInnerData.getTargetData(name)?: return false
        return run(player, name, questData, targetData, con[0].toInt())
    }

}