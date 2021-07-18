package cn.inrhor.questengine.command

import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.command.quest.QuestAccept
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.*

class QuestCommand: BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("@COMMAND.BASE.QUESTID", true) { arrayListOf("accept", "quit") },
        Argument("@COMMAND.BASE.QUESTID", true) { QuestManager.questMap.map { it.key } },
        Argument("@COMMAND.BASE.PLAYER", true)
    )

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        when (args[0].lowercase(Locale.getDefault())) {
            "accept" -> QuestAccept().onCommand(sender, command, label, args)
            "quit" -> QuestAccept().onCommand(sender, command, label, args)
        }
        return
    }

}