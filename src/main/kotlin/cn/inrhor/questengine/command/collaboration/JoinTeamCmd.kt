package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinTeamCmd: BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("@COMMAND.TEAM.NAME", true)
    )

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        if (TeamManager.hasTeam(pUUID)) return run { TLocale.sendTo(player, "TEAM.HAS_TEAM") }
        val teamName = args[0]
        val tData = TeamManager.getTeamData(teamName)?: return run { TLocale.sendTo(player, "TEAM.NO_EXIST_TEAM") }
        TeamManager.addAsk(pUUID, tData)
        return
    }

}