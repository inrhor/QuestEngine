package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestModule
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import java.util.*

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
        val questID = q.questID
        val questUUID = targetData.questUUID
        val innerID = targetData.innerID
        if (q.mode.type == ModeType.COLLABORATION) {
            player.teamData()?.playerMembers()?.forEach {
                sendFinish(it, questID, questUUID, innerID)
            }
        }
        sendFinish(player, questID, questUUID, innerID)
    }

    fun sendFinish(player: Player, innerData: QuestInnerData) {
        if (innerData.isFinishTarget()) {
            QuestManager.getInnerQuestModule(innerData.questID, innerData.innerQuestID)
                ?.let { runEval(player, it.finish) }
        }
    }

    fun sendFinish(player: Player, questID: String, questUUID: UUID, innerID: String) {
        val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val innerData = QuestManager.getInnerQuestData(player, questUUID, innerID)?: return
        if (innerData.isFinishTarget()) {
            runEval(player, innerModule.finish)
        }
    }

}