package cn.inrhor.questengine.common.quest.ui.book

import cn.inrhor.questengine.common.quest.ui.PublicJson
import org.bukkit.entity.Player
import taboolib.platform.util.sendBook
import java.util.*

object BookQuestInfo {

    /**
     * 以书本形式发送当前任务内部内容和状态
     */
    fun open(player: Player, questUUID: UUID) {

        player.sendBook {
            PublicJson.questInfo(player, questUUID).forEach {
                write(it)
            }
        }

    }

}