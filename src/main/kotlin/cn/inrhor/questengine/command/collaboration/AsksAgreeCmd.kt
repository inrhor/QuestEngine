package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AsksAgreeCmd: BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("@COMMAND.BASE.PLAYER", true)
    )

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        if (!TeamManager.hasTeam(pUUID)) return
        val tData = TeamManager.getTeamData(pUUID)?: return
        if (!TeamManager.isLeader(pUUID, tData)) return
        val mName = args[0]
        val m = Bukkit.getPlayer(mName)?: return
        val mUUID = m.uniqueId
        TeamManager.removeAsk(mUUID, tData)
        TeamManager.addMember(mUUID, tData)
        return
    }

}