package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook

object HandbookInner {

    val inner = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                DataStorage.getPlayerData(sender.uniqueId).questDataList.values.map { it.questID }
            }
            dynamic {
                suggestion<ProxyPlayer> { sender, context ->
                    listOf(QuestManager.getQuestData(sender.uniqueId, context.argument(-1)!!)!!.questInnerData.innerQuestID)
                }
                execute<ProxyPlayer> { sender, context, _ ->
                    val p = sender.cast<Player>()
                    p.sendBook {
                        QuestBookBuildManager.innerQuestNoteBuild(p, context.argument(-1)!!, context.argument(0)!!).forEach {
                            write(it)
                        }
                    }
                }
            }
        }
    }

}
