package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object QuestAccept {

    @CommandBody
    val accept = subCommand {
        literal("quest") {
            literal("accept") {
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        QuestManager.questMap.map { it.key }
                        Bukkit.getOnlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { sender, context, _ ->
                        val args = context.args

                        val player = Bukkit.getPlayer(args[1]) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE") }

                        val questID = args[2]
                        if (!QuestManager.questMap.containsKey(questID)) {
                            return@execute
                        }

                        QuestManager.acceptQuest(player, questID)
                    }
                }
            }
        }
        literal("innerQuest") {
            literal("accept") {
                dynamic {
                    suggestion<ProxyCommandSender> { _, context ->
                        Bukkit.getOnlinePlayers().map { it.name }
                        QuestManager.questMap.map { it.key }
                        QuestManager.getQuestModule(context.args[1])?.innerQuestList?.map { it.innerQuestID }
                    }
                    execute<ProxyCommandSender> { sender, context, _ ->
                        val args = context.args

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
    }


}