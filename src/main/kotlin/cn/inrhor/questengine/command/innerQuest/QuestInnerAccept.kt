package cn.inrhor.questengine.command.innerQuest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object QuestInnerAccept {

    val accept = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, _ ->
                    QuestManager.questMap.map { it.key }
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, context ->
                        QuestManager.getQuestModule(context.argument(-2)!!)?.innerQuestList?.map { it.innerQuestID }
                    }
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val args = argument.split(" ")

                        val player = Bukkit.getPlayer(context.argument(-2)!!) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }
                        val uuid = player.uniqueId

                        val questID = context.argument(-1)!!
                        if (!QuestManager.questMap.containsKey(questID)) {
                            return@execute
                        }

                        val innerQuestID = args[0]

                        QuestManager.acceptInnerQuest(player, questID, innerQuestID, true)

                    }
                }
            }
        }
    }
}