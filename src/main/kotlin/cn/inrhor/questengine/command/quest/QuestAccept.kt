package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object QuestAccept {

    val accept = subCommand {
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

