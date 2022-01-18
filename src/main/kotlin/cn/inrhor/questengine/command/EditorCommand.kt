package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.edit.editorHome
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText

internal object EditorCommand {

    @CommandBody
    val home = subCommand {
        execute<Player> { sender, _, _ ->
            sender.editorHome()
        }
    }

    @CommandBody
    val add = subCommand {
        literal("quest") {
            execute<Player> { sender, _, _ ->
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-QUEST-ID"), "")) {
                    info("1: "+it[0]+"   2 "+it[1])
                }
            }
        }
        literal("inner") {

        }
    }
}