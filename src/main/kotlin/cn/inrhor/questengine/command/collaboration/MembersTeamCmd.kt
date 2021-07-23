package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MembersTeamCmd: BaseSubCommand() {

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        if (!TeamManager.hasTeam(pUUID)) return run { TLocale.sendTo(player, "TEAM.NO_TEAM") }
        HasTeam.openMembers(player)
        return
    }

}