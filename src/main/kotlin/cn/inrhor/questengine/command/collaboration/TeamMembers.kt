package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import org.bukkit.entity.Player
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamMembers {

    @CommandBody
    val members = subCommand {
        literal("team") {
            literal("members") {
                dynamic {
                    execute<ProxyPlayer> { sender, _, _ ->
                        val player = sender as Player
                        val pUUID = player.uniqueId
                        if (!TeamManager.hasTeam(pUUID)) return@execute run {
                            sender.sendLang("TEAM.NO_TEAM") }
                        HasTeam.openMembers(player)
                    }
                }
            }
        }
    }


}