package cn.inrhor.questengine.common.quest.ui.chat

import cn.inrhor.questengine.common.quest.ui.PublicJson
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import java.util.*

object QuestChat {

    /**
     * 以聊天形式发送当前任务内部内容和状态
     */
    fun chatNowQuestInfo(player: Player, questUUID: UUID) {
        PublicJson.questInfo(player, questUUID).forEach {
            it.sendTo(adaptPlayer(player))
        }
    }

}