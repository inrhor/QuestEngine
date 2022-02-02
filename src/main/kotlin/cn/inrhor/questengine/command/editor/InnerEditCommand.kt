package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorInner.editorInner
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

object InnerEditCommand {

    @CommandBody
    val edit = subCommand {
        dynamic {
            dynamic {
                execute<Player> { sender, content, argument ->
                    val questID = content.argument(-1)
                    val innerID = argument.split(" ")[0]
                    sender.editorInner(questID, innerID)
                }
            }
        }
    }

}