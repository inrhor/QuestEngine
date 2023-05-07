package cn.inrhor.questengine.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

object EditorCommand {

    val editor = subCommand {
        execute<Player> { sender, _, _ ->
            sender.sendMessage("§7[§bQuestEngine§7] §f游戏内编辑器已于3.4.0版本删除，请移步到网页编辑器")
        }
    }

}