package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.quest.*
import cn.inrhor.questengine.common.quest.manager.QuestManager
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.*

class QuestInnerCommand: BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("@COMMAND.BASE.ARGUMENT", true) { arrayListOf("accept", "finish") },
        Argument("@COMMAND.QUEST.QUEST_ID", true) { QuestManager.questMap.map { it.key } },
        Argument("@COMMAND.QUEST.INNER_QUEST_ID", true),
        Argument("@COMMAND.BASE.PLAYER", true)
    )

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        when (args[0].lowercase(Locale.getDefault())) {
            "accept" -> InnerQuestAccept().onCommand(sender, args)
            "finish" -> InnerQuestFinish().onCommand(sender, args)
        }
        return
    }

}