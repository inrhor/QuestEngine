package cn.inrhor.questengine.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

object EditorCommand {

    val editor = subCommand {
        execute<Player> { sender, _, _ ->
        }
    }

}