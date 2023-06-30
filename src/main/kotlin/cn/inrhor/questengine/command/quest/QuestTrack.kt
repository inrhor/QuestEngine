package cn.inrhor.questengine.command.quest

import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.quest.manager.QuestManager.trackQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager.trackTarget
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang

internal object QuestTrack {

    @CommandBody
    val track = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic("quest") {
                suggestion<ProxyCommandSender> { _, context ->
                    Bukkit.getPlayer(context["player"])?.let { p ->
                        p.getPlayerData().dataContainer.quest.keys.map { it }
                    }
                }
                dynamic("target") {
                    execute<ProxyCommandSender> { sender, context, _ ->
                        val player = Bukkit.getPlayer(context["player"]) ?: return@execute run {
                            sender.sendLang("PLAYER_NOT_ONLINE")
                        }
                        player.trackTarget(context["quest"], context["target"])
                    }
                }
                execute<ProxyCommandSender> { sender, context, _ ->
                    val player = Bukkit.getPlayer(context["player"]) ?: return@execute run {
                        sender.sendLang("PLAYER_NOT_ONLINE")
                    }
                    player.trackQuest(context["quest"])

                }
            }
        }
    }


}