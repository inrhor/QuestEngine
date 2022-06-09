package cn.inrhor.questengine.command.tags

import cn.inrhor.questengine.common.database.data.tagsData
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object TagsAdd {

    val add = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val args = argument.split(" ")
                    val player = Bukkit.getPlayer(context.argument(-1)) ?: return@execute run {
                        sender.sendLang("PLAYER_NOT_ONLINE")
                    }
                    val tags = player.tagsData()
                    val tag = args[0]
                    val pName = player.name
                    tags.addTag(tag)
                    sender.sendLang("COMMAND-TAGS-ADD", pName, tag)
                }
            }
        }
    }

}
