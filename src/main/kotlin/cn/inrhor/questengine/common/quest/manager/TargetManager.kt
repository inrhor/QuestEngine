package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.database.data.teamData
import cn.inrhor.questengine.common.quest.manager.QuestManager.matchQuestMode
import org.bukkit.entity.Player

object TargetManager {

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(player: Player, name: String, targetData: TargetData): Int {
        var schedule = targetData.schedule
        if (targetData.questUUID.matchQuestMode(player, true)) {
            player.teamData()?.playerMembers()?.forEach {
                val innerData = QuestManager.getInnerQuestData(it, targetData.questUUID)
                val tgData = innerData?.getTargetData(name)
                schedule += tgData?.schedule?: 0
            }
        }
        return schedule
    }

    fun runTask(pData: PlayerData, player: Player) {
        pData.questDataList.values.forEach {
            val inner = it.questInnerData
            inner.targetsData.values.forEach { t ->
                if (t.name.lowercase().startsWith("task ")) {
                    t.runTask(player, it, inner, QuestManager.getQuestMode(it.questID))
                }
            }
        }
    }

}