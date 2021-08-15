package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.platform.util.sendLang

object TeamJoin {

    val join = subCommand {
        dynamic {
            suggestion<Player> { _, _ ->
                TeamManager.teamsMap.map { it.key }
            }
            execute<Player> { sender, context, argument ->
                val args = argument.split(" ")
                val pUUID = sender.uniqueId
                if (TeamManager.hasTeam(pUUID)) return@execute run {
                    sender.sendLang("TEAM-HAS_TEAM") }
                val teamName = args[0]
                val tData = TeamManager.getTeamData(teamName)?: return@execute run {
                    sender.sendLang("TEAM-NO_EXIST_TEAM") }
                TeamManager.addAsk(pUUID, tData)
            }
        }
    }


}