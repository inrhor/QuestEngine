package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.dialog.DialogManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

internal object DialogCommand {

    val dialog = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, _ ->
                    DialogManager.getMap().map { it.key }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val args = argument.split(" ")

                    val player = Bukkit.getPlayer(context.argument(-1))?: return@execute run {
                        sender.sendLang("PLAYER_NOT_ONLINE")
                    }

                    val dialogID = args[0]

                    DialogManager.sendDialogHolo(player, dialogID)
                }
            }
        }
    }

}