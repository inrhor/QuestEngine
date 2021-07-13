package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.collaboration.TeamCommand
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.CommandType
import io.izzel.taboolib.module.command.base.SubCommand

@BaseCommand(name = "iqe")
class Command: BaseMainCommand() {

    @SubCommand(permission = "QuestEngine.use", type = CommandType.PLAYER)
    val team = TeamCommand()

}