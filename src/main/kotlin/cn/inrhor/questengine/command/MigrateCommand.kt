package cn.inrhor.questengine.command

import cn.inrhor.questengine.common.quest.MigrateMode
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand

object MigrateCommand {

    val migrate = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            MigrateMode().task()
        }
    }

}