package cn.inrhor.questengine.common.quest.ui.chat

import cn.inrhor.questengine.common.quest.QuestStateUtil
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.time.TimeUtil
import org.bukkit.entity.Player
import taboolib.common.platform.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang
import java.util.*

object QuestChat {

    /**
     * 以聊天形式发送当前任务内部内容和状态
     */
    fun chatNowQuestInfo(player: Player, questUUID: UUID) {
        val innerData = QuestManager.getInnerQuestData(player, questUUID)?: return run {
            player.sendLang("QUEST-NULL_QUEST_DATA", "chatNowQuestInfo")
        }
        val questID = innerData.questID
        val innerID = innerData.innerQuestID

        val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return run {
            player.sendLang("QUEST-ERROR_FILE", questID)
        }

        val ds = UtilString.getJsonStr(innerModule.description)
            .replace("%state%", QuestStateUtil.stateUnit(player, innerData.state), true)
        TellrawJson()
            .append(ds.colored())
            .sendTo(adaptPlayer(player))

        innerModule.questTargetList.forEach { (name, target) ->
            val tData = innerData.targetsData[name]?: return@forEach
            var time = "null"
            val endDate = tData.endTimeDate
            if (endDate != null) {
                time = TimeUtil.remainDate(player, endDate)
            }
            val tds = UtilString.getJsonStr(target.description)
                .replace("%schedule%", tData.schedule.toString(), true)
                .replace("%time%", time, true)
            TellrawJson()
                .append(tds.colored())
                .sendTo(adaptPlayer(player))
        }
    }

}