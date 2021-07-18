package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.collaboration.*
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class TeamCommand: BaseSubCommand() {

    val create: BaseSubCommand = CreateTeamCmd()

    val invite: BaseSubCommand = InviteTeamCmd()

    val join: BaseSubCommand = JoinTeamCmd()

    val kick: BaseSubCommand = KickTeamCmd()

    val del: BaseSubCommand = DelTeamCmd()

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        return
    }

}