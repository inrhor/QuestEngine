package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook

object HandbookSort {

    val sort = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { _, _ ->
                QuestBookBuildManager.sortQuest.map { it.key }
            }
            execute<ProxyPlayer> { sender, context, _ ->
                val p = sender.cast<Player>()
                p.sendBook {
                    QuestBookBuildManager.questSortBuild(p, context.argument(0)!!).forEach {
                        write(it)
                    }
                }
            }
        }
    }

}
