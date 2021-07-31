package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.ui.chat.QuestChat
import org.bukkit.Bukkit
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object QuestInfo {

    @CommandBody
    val info = subCommand {
        literal("quest") {
            literal("info") {
                dynamic {
                    suggestion<ProxyCommandSender> { _, context ->
                        QuestManager.questMap.map { it.key }
                        Bukkit.getPlayer(context.args[1])?.let { p ->
                            DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                        }
                    }
                    execute<ProxyCommandSender> { sender, context, _ ->
                        val args = context.args

                        val player = Bukkit.getPlayer(args[1])?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE") }
                        val uuid = player.uniqueId

                        val questID = args[2]

                        val questData = QuestManager.getQuestData(uuid, questID)?: return@execute run {
                            sender.sendLang("QUEST.NULL_QUEST_DATA", questID) }


                        QuestChat.chatNowQuestInfo(player, questData.questUUID)
                    }
                }
            }
        }
    }


}