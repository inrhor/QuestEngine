package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook

object HandbookInnerList {

    val innerList = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                DataStorage.getPlayerData(sender.uniqueId).questDataList.values.map { it.questUUID.toString() }
            }
            execute<ProxyPlayer> { sender, context, _ ->
                val p = sender.cast<Player>()
                p.sendBook {
                    QuestBookBuildManager.innerQuestListBuild(p, context.argument(0)!!).forEach {
                        write(it)
                    }
                }
            }
        }
    }

}
