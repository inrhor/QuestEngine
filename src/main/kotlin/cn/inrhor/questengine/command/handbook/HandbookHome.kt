package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.questNoteBuild
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.questSortBuild
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.targetNodeBuild
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook

object HandbookHome {

    val handbook = subCommand {
        execute<ProxyPlayer> { sender, _, _ ->
            val p = sender.cast<Player>()
            p.sendBook {
                QuestBookBuildManager.sortHomeUI.forEach {
                    write(it)
                }
            }
        }
        literal("sort", "info", "target") {
            dynamic {
                execute<ProxyPlayer> { sender, context, argument ->
                    val p = sender.cast<Player>()
                    val e = argument.split(" ")[0]
                    when (context.argument(-1)) {
                        "sort" -> {
                            p.questSortBuild(e)
                        }
                        "info" -> {
                            p.questNoteBuild(e)
                        }
                        "target" -> {
                            p.targetNodeBuild(e)
                        }
                    }
                }
            }
        }
    }

}
