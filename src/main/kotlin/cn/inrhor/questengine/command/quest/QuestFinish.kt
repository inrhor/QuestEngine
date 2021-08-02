package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object QuestFinish {

    val finish = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, context ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, context ->
                    Bukkit.getPlayer(context.argument(-1)!!)?.let { p ->
                        DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                    }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val args = argument.split(" ")

                    val player = Bukkit.getPlayer(context.argument(-1)!!) ?: return@execute run {
                        sender.sendLang("PLAYER_NOT_ONLINE")
                    }
                    val uuid = player.uniqueId

                    val questID = args[0]

                    val questData = QuestManager.getQuestData(uuid, questID) ?: return@execute run {
                        sender.sendLang("QUEST.NULL_QUEST_DATA", questID)
                    }

                    QuestManager.endQuest(player, questData, QuestState.FINISH, false)
                }
            }
        }
    }

}