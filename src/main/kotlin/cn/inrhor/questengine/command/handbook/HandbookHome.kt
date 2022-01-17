package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook

object HandbookHome {

    val home = subCommand {
        execute<ProxyPlayer> { sender, _, _ ->
            val p = sender.cast<Player>()
            p.sendBook {
                QuestBookBuildManager.sortHomeUI.forEach {
                    write(it)
                }
            }
        }
    }

}
