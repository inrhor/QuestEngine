package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.editor.ui.EditHome
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

object EditorCommand {

    val editor = subCommand {
        execute<Player> { sender, _, _ ->
            EditHome.open(sender)
        }
    }

}