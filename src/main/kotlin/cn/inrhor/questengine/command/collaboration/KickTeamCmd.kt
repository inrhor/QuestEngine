package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KickTeamCmd: BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("@COMMAND.BASE.PLAYER", true)
    )

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        val teamData = TeamManager.getTeamData(pUUID)?: return run { TLocale.sendTo(player, "TEAM.NO_TEAM") }
        if (!TeamManager.isLeader(pUUID, teamData)) return run { TLocale.sendTo(player, "TEAM.NOT_LEADER") }
        val mName = args[0]
        val m = Bukkit.getPlayer(mName)?: return run { TLocale.sendTo(player, "PLAYER_NOT_ONLINE") }
        val mUUID = m.uniqueId
        TeamManager.removeMember(mUUID, teamData)
        return
    }

}