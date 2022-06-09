package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.database.data.targetData
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.manager.QuestManager.matchMode
import org.bukkit.entity.Player

object TargetManager {

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(player: Player, name: String, targetData: TargetData): Int {
        var schedule = targetData.schedule
        if (targetData.questID.matchMode(player)) {
            player.teamData()?.playerMembers(false)?.forEach {
                val tgData = player.targetData(targetData.questID, targetData.id)
                schedule += tgData.schedule
            }
        }
        return schedule
    }

}