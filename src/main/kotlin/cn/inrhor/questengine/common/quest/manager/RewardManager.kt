package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.module.group.GroupModule
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestModule
import cn.inrhor.questengine.script.kether.runEval
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
                sendFinish(it, q, targetData)
            }
        }
        sendFinish(player, q, targetData)
    }

    fun sendFinish(player: Player, innerData: QuestData) {
        if (innerData.isFinishTarget()) {
            QuestManager.getInnerModule(innerData.questID, innerData.id)
                ?.let { runEval(player, it.finish) }
        }
    }

    fun sendFinish(player: Player, questModule: GroupModule, targetData: TargetData) {
        val questID = questModule.questID
        val questUUID = targetData.questUUID
        val innerID = targetData.innerID
        targetData.state = StateType.FINISH
        val innerModule = QuestManager.getInnerModule(questID, innerID)?: return
        val innerData = QuestManager.getInnerQuestData(player, questUUID, innerID)?: return
        innerData.target[targetData.questTarget.id] = targetData
        if (innerData.isFinishTarget()) {
            runEval(player, "quest select useUid $questUUID inner select $innerID "+innerModule.finish)
        }
    }

}