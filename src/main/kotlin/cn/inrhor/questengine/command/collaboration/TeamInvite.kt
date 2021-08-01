package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object TeamInvite {

    val invite = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            execute<ProxyPlayer> { sender, context, _ ->
                val args = context.args
                val player = sender as Player
                val pUUID = player.uniqueId
                val teamData = TeamManager.getTeamData(pUUID)?: return@execute run {
                    sender.sendLang("TEAM.NO_TEAM") }
                if (!TeamManager.isLeader(pUUID, teamData)) return@execute run {
                    sender.sendLang("TEAM.NOT_LEADER") }
                val mName = args[0]
                val m = Bukkit.getPlayer(mName)?: return@execute run {
                    sender.sendLang("PLAYER_NOT_ONLINE") }
                val mUUID = m.uniqueId
                TeamManager.addMember(mUUID, teamData)
            }
        }
    }


}