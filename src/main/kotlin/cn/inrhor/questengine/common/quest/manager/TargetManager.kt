package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.manager.DataManager.teamData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.api.manager.DataManager.targetData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.matchMode
import org.bukkit.entity.Player

object TargetManager {

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(player: Player, targetData: TargetData): Int {
        var schedule = targetData.schedule
        if (targetData.questID.matchMode(player)) {
            player.teamData()?.playerMembers(false)?.forEach {
                val tgData = player.targetData(targetData.questID, targetData.id)
                schedule += tgData?.schedule?: 0
            }
        }
        return schedule
    }

    /**
     * 修改目标状态
     */
    fun toggleTarget(player: Player, questID: String, targetID: String, state: StateType = StateType.DOING) {
        player.targetData(questID, targetID)?.state = state
    }

}