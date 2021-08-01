package cn.inrhor.questengine.command.innerQuest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object QuestInnerFinish {

    val finish = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, context ->
                Bukkit.getOnlinePlayers().map { it.name }
                Bukkit.getPlayer(context.argument(1))?.let { p ->
                    DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                }
                QuestManager.getQuestModule(context.argument(2))?.innerQuestList?.map { it.innerQuestID }
            }
            execute<ProxyCommandSender> { sender, context, _ ->
                val args = context.arguments()

                val player = Bukkit.getPlayer(args[1])?: return@execute run {
                    sender.sendLang("PLAYER_NOT_ONLINE") }
                val uuid = player.uniqueId

                val questID = args[2]

                val questData = QuestManager.getQuestData(uuid, questID)?: return@execute run {
                    sender.sendLang("QUEST.NULL_QUEST_DATA", questID) }

                val innerQuestID = args[3]

                QuestManager.finishInnerQuest(player, questData.questUUID, questID, innerQuestID)
            }
        }
    }

}