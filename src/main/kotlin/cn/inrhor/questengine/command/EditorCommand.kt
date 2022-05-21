package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.editor.EditorHome.editorHome
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand

object EditorCommand {

    @CommandBody
    val editor = subCommand {
        execute<Player> { sender, _, _ ->
            sender.editorHome()
        }
    }

}