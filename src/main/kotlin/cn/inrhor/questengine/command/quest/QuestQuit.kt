package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.quest.manager.QuestManager.quitQuest
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

internal object QuestQuit {

    @CommandBody
    val quit = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                    suggestion<ProxyCommandSender> { _, context ->
                        Bukkit.getPlayer(context.argument(-1))?.let { p ->
                            p.getPlayerData().dataContainer.quest.keys.map { it }
                        }
                    }
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val args = argument.split(" ")

                        val player = Bukkit.getPlayer(context.argument(-1)) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }

                        player.quitQuest(args[0])

                    }
            }
        }
    }


}