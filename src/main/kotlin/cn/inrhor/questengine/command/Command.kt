package cn.inrhor.questengine.command

import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.command.base.SubCommand

@BaseCommand(name = "QuestEngine")
class Command: BaseMainCommand() {

    @SubCommand(permission = "QuestEngine.admin.quest")
    val quest: BaseSubCommand = QuestCommand()

    @SubCommand(permission = "QuestEngine.use.team")
    val team: BaseSubCommand = TeamCommand()

}