package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.ui.book.BookQuestInfo
import cn.inrhor.questengine.common.quest.ui.chat.QuestChat
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object QuestInfo {

    val info = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, context ->
                    Bukkit.getPlayer(context.argument(-1)!!)?.let { p ->
                        DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                    }
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        listOf("book", "chat")
                    }
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val args = argument.split(" ")

                        val player = Bukkit.getPlayer(context.argument(-2)!!) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }
                        val uuid = player.uniqueId

                        val questID = context.argument(-1)!!

                        val questData = QuestManager.getQuestData(uuid, questID) ?: return@execute run {
                            sender.sendLang("QUEST-NULL_QUEST_DATA", questID)
                        }

                        when (args[0]) {
                            "book" -> BookQuestInfo.open(player, questData.questUUID)
                            "chat" -> QuestChat.chatNowQuestInfo(player, questData.questUUID)
                        }
                    }
                }
            }
        }
    }
}
