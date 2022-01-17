package cn.inrhor.questengine.command.tags

import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object TagsList {

    val list = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, _, argument ->
                val args = argument.split(" ")
                val player = Bukkit.getPlayer(args[0]) ?: return@execute run {
                    sender.sendLang("PLAYER_NOT_ONLINE")
                }
                val pData = DataStorage.getPlayerData(player.uniqueId)
                val tags = pData.tagsData
                val pName = player.name
                sender.sendLang("TAGS-LIST", pName)
                tags.list().forEach {
                    sender.sendLang("TAGS-LIST-FORMAT", it)
                }
            }
        }
    }

}
