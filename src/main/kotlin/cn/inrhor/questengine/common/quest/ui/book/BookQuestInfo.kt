package cn.inrhor.questengine.common.quest.ui.book

import cn.inrhor.questengine.common.quest.QuestStateUtil
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.entity.Player
import taboolib.module.chat.HexColor
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.util.BookBuilder
import taboolib.platform.util.sendLang
import java.util.*

object BookQuestInfo {

    fun open(player: Player, questUUID: UUID) {
        val innerData = QuestManager.getInnerQuestData(player, questUUID)?: return run {
            player.sendLang("QUEST-NULL_QUEST_DATA", "chatNowQuestInfo")
        }
        val questID = innerData.questID
        val innerID = innerData.innerQuestID

        val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return run {
            player.sendLang("QUEST-ERROR_FILE", questID)
        }

        val bookBuilder = BookBuilder()



        bookBuilder.writeRaw()

        player.openBook(bookBuilder.build())
    }

}