package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.toState
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

object QuestState {

    val state = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { _, context ->
                    Bukkit.getPlayer(context.argument(-1)!!)?.let { p ->
                        DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                    }
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        listOf("DOING", "IDLE")
                    }
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val args = argument.split(" ")

                        val player = Bukkit.getPlayer(context.argument(-2)!!) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }

                        val questID = context.argument(-1)!!
                        val state = args[0].toState()

                        QuestManager.setQuestState(player, questID, state)
                    }
                }
            }
        }
    }

}