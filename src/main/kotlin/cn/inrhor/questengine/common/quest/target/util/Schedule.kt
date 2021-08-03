package cn.inrhor.questengine.common.quest.target.util

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.RewardManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.common.quest.target.TargetBreakBlock
import org.bukkit.entity.Player

object Schedule {

    fun run(player: Player,
            questData: QuestData,
            questInnerData: QuestInnerData,
            target: QuestTarget,
            targetData: TargetData, amount: Int): Boolean {
        if (targetData.schedule < amount) {
            targetData.schedule = targetData.schedule + 1
            return true
        }
        val allSchedule = TargetManager.scheduleUtil(TargetBreakBlock.name, questData, targetData)
        return RewardManager.finishReward(player, questData, questInnerData, target, amount, allSchedule)
    }

}