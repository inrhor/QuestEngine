package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook

object HandbookInfo {

    val info = subCommand {
        dynamic {
            execute<ProxyPlayer> { sender, context, argument ->
                val p = sender.cast<Player>()
                val args = argument.split(" ")
                val qUUID = if (args.size >= 2) args[1] else ""
                p.sendBook {
                    QuestBookBuildManager.questNoteBuild(p, args[0], qUUID).forEach {
                        write(it)
                    }
                }
            }
        }
    }

}
