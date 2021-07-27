package cn.inrhor.questengine.common.quest.ui.chat

import cn.inrhor.questengine.common.quest.QuestStateUtil
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.public.UtilString
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.entity.Player
import java.util.*

object QuestChat {

    /**
     * 以聊天形式发送当前任务内部内容和状态
     */
    fun chatNowQuestInfo(player: Player, questUUID: UUID) {
        val innerData = QuestManager.getInnerQuestData(player, questUUID)?: return run {
            TLocale.sendTo(player, "QUEST.NULL_QUEST_DATA", "chatNowQuestInfo")
        }
        val questID = innerData.questID
        val innerID = innerData.innerQuestID

        val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return run {
            TLocale.sendTo(player, "QUEST.ERROR_FILE", questID)
        }

        val ds = UtilString.getJsonStr(innerModule.description)
            .replace("%state%", QuestStateUtil.stateUnit(innerData.state), true)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(ds))
            .send(player)

        innerModule.questTargetList.forEach { (name, target) ->
            val tData = innerData.targetsData[name]?: return@forEach
            val time = tData.time.toString()
            val tds = UtilString.getJsonStr(target.description)
                .replace("%schedule%", tData.schedule.toString(), true)
                .replace("%time%", time+" "+timeUnit(target), true)
            TellrawJson.create()
                .append(TLocale.Translate.setColored(tds))
                .send(player)
        }
    }

    /**
     * 时间单位
     */
    fun timeUnit(target: QuestTarget): String {
        when (target.time.lowercase(Locale.getDefault())) {
            "minute" -> return TLocale.asString("QUEST.TIME_MINUTE")
        }
        return TLocale.asString("QUEST.TIME_S")
    }

}