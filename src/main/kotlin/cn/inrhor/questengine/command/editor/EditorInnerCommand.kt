package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.common.edit.EditorList.editorListInner
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

internal object EditorInnerCommand {

    @CommandBody
    val home = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody
    val list = subCommand {
        dynamic {
            dynamic {
                execute<Player> { sender, content, argument ->
                    val args = argument.split(" ")
                    sender.editorListInner(content.argument(-1), args[0].toInt())
                }
            }
            execute<Player> { sender, _, argument ->
                val args = argument.split(" ")
                sender.editorListInner(args[0])
            }
        }
    }

}