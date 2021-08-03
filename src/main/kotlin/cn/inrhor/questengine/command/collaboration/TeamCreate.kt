package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.entity.Player
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamCreate {

    val create = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                val pUUID = sender.uniqueId
                if (TeamManager.hasTeam(pUUID)) return@execute run {
                    sender.sendLang("TEAM-HAS_TEAM") }
                val teamName = argument.split(" ")[0]
                TeamManager.createTeam(teamName, pUUID)
            }
        }
    }


}