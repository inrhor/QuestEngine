package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object TagsCommand {

    val admin = mainCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, _ ->
                    listOf("add", "remove", "list")
                }
                dynamic {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val args = argument.split(" ")

                        val player = Bukkit.getPlayer(context.argument(-2)!!) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }

                        val pData = DataStorage.getPlayerData(player.uniqueId)
                        val tags = pData.tagsData

                        val tag = args[0]
                        val pName = player.name

                        when (context.argument(-1)) {
                            "add" -> {
                                tags.addTag(tag)
                                sender.sendLang("COMMAND-TAGS-ADD", pName, tag)
                            }
                            "remove" -> {
                                tags.removeTag(tag)
                                sender.sendLang("OMMAND-TAGS-REMOVE", pName, tag)
                            }
                            "list" -> {
                                sender.sendLang("TAGS-LIST", pName)
                                tags.list().forEach {
                                    sender.sendLang("TAGS-LIST-FORMAT", it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
