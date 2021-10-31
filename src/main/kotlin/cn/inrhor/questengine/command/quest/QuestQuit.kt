package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang
import java.util.*

internal object QuestQuit {

    @CommandBody
    val quit = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, _ ->
                    listOf("id", "uuid")
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, context ->
                        val pMap = Bukkit.getPlayer(context.argument(-2))
                        if (context.argument(-1) == "id") {
                            pMap?.let { p ->
                                DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                            }
                        }else {
                            pMap?.let { p ->
                                DataStorage.getPlayerData(p).questDataList.values.map { it.questUUID.toString() }
                            }
                        }
                    }
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val args = argument.split(" ")

                        val player = Bukkit.getPlayer(context.argument(-2)) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }

                        if (context.argument(-1) == "id") {
                            QuestManager.quitQuest(player, args[0])
                        }else {
                            QuestManager.quitQuest(player, UUID.fromString(args[0]))
                        }

                    }
                }
            }
        }
    }


}