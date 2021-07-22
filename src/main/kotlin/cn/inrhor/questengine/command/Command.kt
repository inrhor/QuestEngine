package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.collaboration.*
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.command.base.SubCommand

@BaseCommand(name = "QuestEngine")
class Command: BaseMainCommand() {

    @SubCommand(permission = "QuestEngine.admin.quest")
    val quest: BaseSubCommand = QuestCommand()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamOpen: BaseSubCommand = OpenTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamCreate: BaseSubCommand = CreateTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamJoin: BaseSubCommand = JoinTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamLeave: BaseSubCommand = LeaveTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamMembers: BaseSubCommand = MembersTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamDelete: BaseSubCommand = DelTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamInvite: BaseSubCommand = InviteTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team")
    val teamKick: BaseSubCommand = KickTeamCmd()

}