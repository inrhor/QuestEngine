package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.Bukkit
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

internal object QuestQuit {

    @CommandBody
    val quit = subCommand {
        literal("quest") {
            literal("quit") {
                dynamic {
                    suggestion<ProxyCommandSender> { _, context ->
                        Bukkit.getOnlinePlayers().map { it.name }
                        Bukkit.getPlayer(context.argument(1))?.let { p ->
                            DataStorage.getPlayerData(p).questDataList.values.map { it.questID }
                        }
                    }
                    execute<ProxyCommandSender> { sender, context, _ ->
                        val args = context.arguments()

                        val player = Bukkit.getPlayer(args[1])?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE") }

                        val questID = args[2]

                        QuestManager.quitQuest(player, questID)
                    }
                }
            }
        }
    }


}