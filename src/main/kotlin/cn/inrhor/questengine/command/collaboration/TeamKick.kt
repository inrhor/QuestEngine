package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamKick {

    val kick = subCommand {
        dynamic {
            suggestion<Player> { sender, _ ->
                TeamManager.getTeamData(sender)?.members?.map {
                    (Bukkit.getPlayer(it))?.name.toString() }
            }
            execute<Player> { sender, context, _ ->
                val args = context.arguments()
                val pUUID = sender.uniqueId
                val teamData = TeamManager.getTeamData(pUUID)?: return@execute run {
                    sender.sendLang("TEAM.NO_TEAM") }
                if (!TeamManager.isLeader(pUUID, teamData)) return@execute run {
                    sender.sendLang("TEAM.NOT_LEADER") }
                val mName = args[0]
                val m = Bukkit.getPlayer(mName)?: return@execute run {
                    sender.sendLang("PLAYER_NOT_ONLINE") }
                val mUUID = m.uniqueId
                TeamManager.removeMember(mUUID, teamData)
            }
        }
    }


}