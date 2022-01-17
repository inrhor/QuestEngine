package cn.inrhor.questengine.command.handbook

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.platform.util.sendBook
import java.util.*

object HandbookTarget {

    val target = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                DataStorage.getPlayerData(sender.uniqueId).questDataList.values.map { it.questUUID.toString() }
            }
            execute<ProxyPlayer> { sender, context, _ ->
                val p = sender.cast<Player>()
                val questUUID = UUID.fromString(context.argument(0))
                p.sendBook {
                    QuestBookBuildManager.targetNodeBuild(p, questUUID, QuestManager.getInnerQuestData(p, questUUID)!!.innerQuestID).forEach {
                        write(it)
                    }
                }
            }
        }
    }

}
