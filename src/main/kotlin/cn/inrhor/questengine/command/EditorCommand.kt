package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.edit.EditorHome.editorHome
import cn.inrhor.questengine.common.edit.EditorList.editorListQuest
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
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
    val list = subCommand {
        execute<Player> { sender, _, _ ->
            sender.editorListQuest()
        }
    }

    @CommandBody
    val add = subCommand {
        literal("quest") {
            execute<Player> { sender, _, _ ->
                sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                    // 获取任务ID则替换空格为""
                }
            }
        }
        literal("inner") {

        }
    }
}