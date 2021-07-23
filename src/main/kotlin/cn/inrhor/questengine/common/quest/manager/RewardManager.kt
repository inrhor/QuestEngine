package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestOpenData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestReward
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.script.kether.KetherHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object RewardManager {

    /**
     * 满足进度触发奖励
     */
    fun finishReward(player: Player, questData: QuestData, questOpenData: QuestOpenData, target: QuestTarget, amount: Int, schedule: Int): Boolean {
        if (schedule >= amount) {
            val questID = questData.questID
            if (QuestManager.getQuestMode(questID) == ModeType.COLLABORATION) {
                val team = questData.teamData ?: return false
                for (mUUID in team.members) {
                    val m = Bukkit.getPlayer(mUUID)?: continue
                    finishReward(m, questOpenData, target)
                }
            }else {
                finishReward(player, questOpenData, target)
            }
        }
        return true
    }

    private fun finishReward(player: Player, questOpenData: QuestOpenData, target: QuestTarget) {
        val mainID = questOpenData.mainQuestID
        val subID = questOpenData.subQuestID
        val questID = questOpenData.questID
        if (subID == "") {
            val mainModule = QuestManager.getMainQuestModule(questID, mainID)?: return
            val rewardRepeatState = questOpenData.rewardState[target.name]?: false
            finishReward(player, questOpenData, mainModule.questReward, target.reward, rewardRepeatState)
        }else {
            val subModule = QuestManager.getSubQuestModule(questID, mainID, subID)?: return
            val rewardRepeatState = questOpenData.rewardState[target.name]?: false
            finishReward(player, questOpenData, subModule.questReward, target.reward, rewardRepeatState)
        }
        questOpenData.rewardState[target.name] = true
    }

    private fun finishReward(player: Player, questOpenData: QuestOpenData, questReward: QuestReward, content: String, repeat: Boolean) {
        val s = content.split(" ")
        val rewardID = s[0]
        val repeatModule = s[1].toBoolean()
        finishReward(player, questReward, rewardID, repeatModule, repeat)
        questOpenData.rewardState[rewardID] = true
    }

    private fun finishReward(player: Player, questReward: QuestReward, rewardID: String, repeatModule: Boolean, repeat: Boolean) {
        if (!repeatModule && repeat) return
        if (rewardID == "all") {
            questReward.finishReward.values.forEach { list ->
                list.forEach {
                    KetherHandler.eval(player, it)
                }
            }
        }else {
            val reward = questReward.finishReward[rewardID] ?: return
            reward.forEach {
                KetherHandler.eval(player, it)
            }
        }
    }

}