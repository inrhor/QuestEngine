package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object TeamJoin {

    val join = subCommand {
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