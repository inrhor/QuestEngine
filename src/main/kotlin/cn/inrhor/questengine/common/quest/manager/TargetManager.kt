package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TargetManager {

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(name: String, questData: QuestData, targetData: TargetData): Int {
        val questModule = QuestManager.getQuestModule(questData.questID)?: return 0
        val mode = questModule.mode
        if (mode.type == ModeType.COLLABORATION && mode.shareData && questData.teamData != null) {
            var schedule = 0
            for (mUUID in questData.teamData!!.members) {
                val m = Bukkit.getPlayer(mUUID)?: continue
                val innerData = QuestManager.getInnerQuestData(m, questData.questUUID)?: continue
                val tgData = innerData.targetsData[name]?: continue
                schedule += tgData.schedule
            }
            return schedule
        }
        if (questModule.mode.type == ModeType.PERSONAL) {
            return targetData.schedule
        }
        return 0
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