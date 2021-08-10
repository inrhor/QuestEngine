package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.toUnit
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.time.TimeUtil
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang
import java.util.*

object PublicJson {

    fun questInfo(player: Player, questUUID: UUID): MutableList<TellrawJson> {
        val jsList = mutableListOf<TellrawJson>()

        val innerData = QuestManager.getInnerQuestData(player, questUUID)
        if (innerData == null) {
            player.sendLang("QUEST-NULL_QUEST_DATA", "chatNowQuestInfo")
            return jsList
        }

        val questID = innerData.questID
        val innerID = innerData.innerQuestID

        val innerModule = QuestManager.getInnerQuestModule(questID, innerID)
        if (innerModule == null) {
            player.sendLang("QUEST-ERROR_FILE", questID)
            return jsList
        }

        val ds = UtilString.getJsonStr(innerModule.description)
            .replace("%state%", innerData.state.toUnit(player), true)
        val dsJs = TellrawJson()
            .append(ds.colored())
        jsList.add(dsJs)

        innerModule.questTargetList.forEach { (name, target) ->
            val tData = innerData.targetsData[name]?: return@forEach
            var time = "null"
            val endDate = tData.endTimeDate
            if (endDate != null) {
                time = TimeUtil.remainDate(player, innerData.state, endDate)
            }
            val tds = UtilString.getJsonStr(target.description)
                .replace("%schedule%", tData.schedule.toString(), true)
                .replace("%time%", time, true)
            val tdsJs = TellrawJson()
                .append(tds.colored())
            jsList.add(tdsJs)
        }

        return jsList
    }

}