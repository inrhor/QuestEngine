package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.entity.Player
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamLeave {

    val leave = subCommand {
        dynamic {
            execute<Player> { sender, _, _ ->
                val pUUID = sender.uniqueId
                val teamData = TeamManager.getTeamData(pUUID)?: return@execute run {
                    sender.sendLang("TEAM.NO_TEAM") }
                TeamManager.removeMember(pUUID, teamData)
            }
        }
    }


}