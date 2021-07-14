package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinTeamCmd: BaseSubCommand() {

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        if (TeamManager.hasTeam(pUUID)) return
        val teamName = args[0]
        if (!TeamManager.teamsMap.containsKey(teamName)) return
        val tData = TeamManager.getTeamData(teamName)?: return
        TeamManager.addAsk(pUUID, tData)
        return
    }

}