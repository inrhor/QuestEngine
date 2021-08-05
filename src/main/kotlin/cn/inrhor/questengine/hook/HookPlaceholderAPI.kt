package cn.inrhor.questengine.hook

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.QuestStateUtil
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.time.TimeUtil
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion
import taboolib.platform.util.asLangText
import java.util.*

object HookPlaceholderAPI: PlaceholderExpansion {

    override val identifier = "questengine"

    override fun onPlaceholderRequest(player: Player, params: String): String {
        val args = params.split("_")
        val questID = args[1]
        val innerID = if (args.size > 1) args[2] else ""
        val index = if (args.size > 2) args[3].toInt() else 1
        return when (args[0].lowercase(Locale.getDefault())) {
            "state" -> getState(player, questID, "")
            "stateinner" -> getState(player, questID, innerID)
            "schedule" -> getSchedule(player, questID, innerID, index)
            "remain" -> remain(player, questID, innerID, index)
            "starttime" -> startTime(player, questID, innerID, index)
            "endtime" -> endTime(player, questID, innerID, index)
            else -> "null"
        }
    }

    private fun endTime(player: Player, questID: String, innerID: String, index: Int): String {
        val targetData = getTargetData(player, questID, innerID, index)?:
        return player.asLangText("QUEST-ALWAYS")?: "always null"
        val time = targetData.endTimeDate?: return player.asLangText("QUEST-ALWAYS")?: "always null"
        return TimeUtil.dateToStr(time)
    }

    private fun startTime(player: Player, questID: String, innerID: String, index: Int): String {
        val targetData = getTargetData(player, questID, innerID, index)?:
        return player.asLangText("QUEST-ALWAYS")?: "always null"
        val time = targetData.timeDate
        return TimeUtil.dateToStr(time)
    }

    /**
     * 剩余时间
     */
    private fun remain(player: Player, questID: String, innerID: String, index: Int): String {
        val always = player.asLangText("QUEST-ALWAYS")?: "always null"
        val uuid = player.uniqueId
        val qData = QuestManager.getQuestData(uuid, questID)?: return always
        val innerData = QuestManager.getInnerQuestData(player, qData.questUUID, innerID)?: return always
        val targetData = getTarget(innerData.targetsData.values, index)?: return always
        val endTime = targetData.endTimeDate?: return always
        return TimeUtil.remainDate(player, innerData.state, endTime)
    }

    private fun getTargetData(player: Player, questID: String, innerID: String, index: Int): TargetData? {
        val uuid = player.uniqueId
        val qData = QuestManager.getQuestData(uuid, questID)?: return null
        val innerData = QuestManager.getInnerQuestData(player, qData.questUUID, innerID)?: return null
        return getTarget(innerData.targetsData.values, index)
    }

    private fun getSchedule(player: Player, questID: String, innerID: String, index: Int): String {
        val schedule = "0"
        val uuid = player.uniqueId
        val qData = QuestManager.getQuestData(uuid, questID)?: return schedule
        val innerData = QuestManager.getInnerQuestData(player, qData.questUUID, innerID)?: return schedule
        val targetData = getTarget(innerData.targetsData.values, index)?: return schedule
        return targetData.schedule.toString()
    }

    // 目标列表索引
    private fun getTarget(list: MutableCollection<TargetData>, index: Int): TargetData? {
        var i = 1
        list.forEach {
            if (i == index) return it
            i++
        }
        return null
    }

    private fun getState(player: Player, questID: String, innerID: String): String {
        val uuid = player.uniqueId
        val qData = QuestManager.getQuestData(uuid, questID)?:
        return player.asLangText("QUEST-STATE_NOT_ACCEPT")?: "state null"
        val state = QuestStateUtil.stateUnit(player, qData.state)
        if (innerID.isNotEmpty()) {
            val innerData = QuestManager.getInnerQuestData(player, qData.questUUID)?: return state
            return QuestStateUtil.stateUnit(player, innerData.state)
        }
        return state
    }
}