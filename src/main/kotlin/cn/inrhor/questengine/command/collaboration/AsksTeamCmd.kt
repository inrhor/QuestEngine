package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AsksTeamCmd: BaseSubCommand() {

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        if (!TeamManager.hasTeam(pUUID)) return
        val tData = TeamManager.getTeamData(pUUID)?: return
        if (!TeamManager.isLeader(pUUID, tData)) return
        HasTeam.openAsks(player)
        return
    }

}