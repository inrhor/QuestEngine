package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.existQuestFrame
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
                    QuestManager.getQuestMap().map { it.key }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val args = argument.split(" ")

                    val player = Bukkit.getPlayer(context.argument(-1)) ?: return@execute run {
                        sender.sendLang("PLAYER_NOT_ONLINE")
                    }

                    val questID = args[0]
                    if (!questID.existQuestFrame()) {
                        return@execute
                    }

                    player.acceptQuest(questID)
                }
            }
        }
    }
}

