package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.api.quest.module.inner.QuestReward
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object RewardManager {

    /**
     * 满足进度触发奖励
     */
    fun finishReward(player: Player, questData: QuestData, questInnerData: QuestInnerData, targetData: TargetData, amount: Int, schedule: Int): Boolean {
        if (schedule >= amount) {
            finishReward(player, questData, questInnerData, targetData)
        }
        return true
    }

    /**
     * 直接触发奖励
     */
    fun finishReward(player: Player, questData: QuestData, questInnerData: QuestInnerData, targetData: TargetData): Boolean {
        val questID = questData.questID
        if (QuestManager.getQuestMode(questID) == ModeType.COLLABORATION) {
            val team = questData.teamData ?: return false
            for (mUUID in team.members) {
                val m = Bukkit.getPlayer(mUUID)?: continue
                finishReward(m, questInnerData, targetData)
            }
        }else {
            finishReward(player, questInnerData, targetData)
        }
        return true
    }

    private fun finishReward(player: Player, questInnerData: QuestInnerData, targetData: TargetData) {
        val mainID = questInnerData.innerQuestID
        val questID = questInnerData.questID
        val innerModule = QuestManager.getInnerQuestModule(questID, mainID)?: return
        finishReward(player, questInnerData, innerModule.reward, targetData.questTarget.reward)
    }

    private fun finishReward(player: Player, questInnerData: QuestInnerData, questReward: QuestReward, content: String) {
        val s = content.split(" ")
        val rewardID = s[0]
        val repeatModule = s[1].toBoolean()
        val rewardRepeatState = questInnerData.rewardState[rewardID]?: false
        finishReward(player, questReward, rewardID, repeatModule, rewardRepeatState)
        questInnerData.rewardState[rewardID] = true
    }

    private fun finishReward(player: Player, questReward: QuestReward, rewardID: String, repeatModule: Boolean, repeat: Boolean) {
        if (!repeatModule && repeat) return
        if (rewardID == "all") {
            questReward.finish.forEach {
                runEval(player, it.script)
            }
        }else {
            val reward = questReward.getFinishScript(rewardID)
            reward.forEach {
                runEval(player, it)
            }
        }
    }

}