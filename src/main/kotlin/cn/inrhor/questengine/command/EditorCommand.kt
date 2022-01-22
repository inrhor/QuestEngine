package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.editor.EditorQuestCommand
import cn.inrhor.questengine.common.edit.EditorHome.editorHome
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

internal object EditorCommand {

    @CommandBody
    val home = subCommand {
        execute<Player> { sender, _, _ ->
            sender.editorHome()
        }
    }

    @CommandBody
    val quest = EditorQuestCommand

}