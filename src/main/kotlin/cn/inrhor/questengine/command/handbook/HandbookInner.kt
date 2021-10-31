package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook
import java.util.*

object HandbookInner {

    val inner = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ -> // questUUID
                DataStorage.getPlayerData(sender.uniqueId).questDataList.values.map { it.questUUID.toString() }
            }
            dynamic {
                suggestion<ProxyPlayer> { sender, context ->
                    listOf(QuestManager.getQuestData(sender.cast(), UUID.fromString(context.argument(-1)))!!.questInnerData.innerQuestID)
                }
                execute<ProxyPlayer> { sender, context, _ -> // innerID
                    val p = sender.cast<Player>()
                    val qUUID = UUID.fromString(context.argument(-1))
                    p.sendBook {
                        QuestBookBuildManager.innerQuestNoteBuild(p, qUUID, context.argument(0)).forEach {
                            write(it)
                        }
                    }
                }
            }
        }
    }

}
