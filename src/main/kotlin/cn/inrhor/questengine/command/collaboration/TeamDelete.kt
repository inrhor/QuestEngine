package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.entity.Player
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object TeamDelete {

    @CommandBody
    val delete = subCommand {
        literal("team") {
            literal("delete") {
                dynamic {
                    execute<ProxyPlayer> { sender, context, _ ->
                        val player = sender as Player
                        val pUUID = player.uniqueId
                        val teamData = TeamManager.getTeamData(pUUID)?: return@execute run {
                            sender.sendLang("TEAM.NO_TEAM") }
                        teamData.delTeam()
                    }
                }
            }
        }
    }


}