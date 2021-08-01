package cn.inrhor.questengine.command.innerQuest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object QuestInnerAccept {

    val accept = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, context ->
                Bukkit.getOnlinePlayers().map { it.name }
                QuestManager.questMap.map { it.key }
                QuestManager.getQuestModule(context.argument(1))?.innerQuestList?.map { it.innerQuestID }
            }
            execute<ProxyCommandSender> { sender, context, _ ->
                val args = context.arguments()

                val player = Bukkit.getPlayer(args[1])?: return@execute run {
                    sender.sendLang("PLAYER_NOT_ONLINE") }
                val uuid = player.uniqueId

                val questID = args[2]
                if (!QuestManager.questMap.containsKey(questID)) {
                    return@execute
                }

                val innerQuestID = args[3]

                val questData = QuestManager.getQuestData(uuid, questID)?: return@execute run {
                    sender.sendLang("QUEST.NULL_QUEST_DATA") }

                QuestManager.acceptInnerQuest(player, questData, innerQuestID, true)

            }
        }
    }
}