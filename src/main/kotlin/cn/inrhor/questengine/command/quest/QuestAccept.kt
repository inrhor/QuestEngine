package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object QuestAccept {

    val accept = subCommand {
        // player
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            // questID
            dynamic {
                suggestion<ProxyCommandSender> { _, _ ->
                    QuestManager.questMap.map { it.key }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val args = argument.split(" ")

                    val player = Bukkit.getPlayer(context.argument(-1)!!) ?: return@execute run {
                        sender.sendLang("PLAYER_NOT_ONLINE")
                    }

                    val questID = args[0]
                    if (!QuestManager.questMap.containsKey(questID)) {
                        return@execute
                    }

                    QuestManager.acceptQuest(player, questID)
                }
            }
        }
    }
}

