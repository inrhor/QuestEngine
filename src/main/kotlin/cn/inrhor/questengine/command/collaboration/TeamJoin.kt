package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.entity.Player
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object TeamJoin {

    @CommandBody
    val join = subCommand {
        literal("team") {
            literal("join") {
                dynamic {
                    suggestion<ProxyPlayer> { _, _ ->
                        TeamManager.teamsMap.map { it.key }
                    }
                    execute<ProxyPlayer> { sender, context, _ ->
                        val args = context.args
                        val player = sender as Player
                        val pUUID = player.uniqueId
                        if (TeamManager.hasTeam(pUUID)) return@execute run {
                            sender.sendLang("TEAM.HAS_TEAM") }
                        val teamName = args[0]
                        val tData = TeamManager.getTeamData(teamName)?: return@execute run {
                            sender.sendLang("TEAM.NO_EXIST_TEAM") }
                        TeamManager.addAsk(pUUID, tData)
                    }
                }
            }
        }
    }


}