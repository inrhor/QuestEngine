package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.api.quest.module.inner.QuestReward
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestID
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestModule
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object RewardManager {

    /**
     * 满足进度触发奖励
     */
    fun finishReward(player: Player, targetData: TargetData, amount: Int, schedule: Int): Boolean {
        if (schedule >= amount) {
            finishReward(player, targetData)
        }
        return true
    }

    /**
     * 直接触发奖励
     */
    fun finishReward(player: Player, targetData: TargetData) {
        val q = targetData.questUUID.getQuestModule(player)?: return
        if (q.mode.type == ModeType.COLLABORATION) {
            player.teamData()?.playerMembers()?.forEach {
                sendFinish(it, targetData)
            }
            sendFinish(player, targetData)
        }else {
            sendFinish(player, targetData)
        }
    }

    private fun sendFinish(player: Player, targetData: TargetData) {
        val innerID = targetData.innerID
        val questID = targetData.questUUID.getQuestID(player)
        val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val innerData = QuestManager.getInnerQuestData(player, targetData.questUUID, innerID)?: return
        finishReward(player, innerData, innerModule.reward, targetData.questTarget.reward)
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
            runEval(player, reward)
        }
    }

}