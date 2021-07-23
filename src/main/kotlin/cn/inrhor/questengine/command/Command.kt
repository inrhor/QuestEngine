package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.collaboration.*
import io.izzel.taboolib.module.command.base.*

@BaseCommand(name = "QuestEngine")
class Command: BaseMainCommand() {

    @SubCommand(permission = "QuestEngine.admin.quest", description = "@COMMAND.QUEST.HANDLE")
    val quest: BaseSubCommand = QuestCommand()

    @SubCommand(permission = "QuestEngine.admin.reload", description = "@COMMAND.RELOAD")
    val reload: BaseSubCommand = ReloadCommand()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.OPEN", type = CommandType.PLAYER)
    val teamOpen: BaseSubCommand = OpenTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.CREATE", type = CommandType.PLAYER)
    val teamCreate: BaseSubCommand = CreateTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.JOIN", type = CommandType.PLAYER)
    val teamJoin: BaseSubCommand = JoinTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.LEAVE", type = CommandType.PLAYER)
    val teamLeave: BaseSubCommand = LeaveTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.MEMBERS", type = CommandType.PLAYER)
    val teamMembers: BaseSubCommand = MembersTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.ASKS", type = CommandType.PLAYER)
    val teamAsks: BaseSubCommand = AsksTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.ASKS_AGREE", type = CommandType.PLAYER)
    val teamAsksAgree: BaseSubCommand = AsksAgreeCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.ASKS_REJECT", type = CommandType.PLAYER)
    val teamAsksReject: BaseSubCommand = AsksRejectCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.DELETE", type = CommandType.PLAYER)
    val teamDelete: BaseSubCommand = DelTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.INVITE", type = CommandType.PLAYER)
    val teamInvite: BaseSubCommand = InviteTeamCmd()

    @SubCommand(permission = "QuestEngine.use.team", description = "@COMMAND.TEAM.KICK", type = CommandType.PLAYER)
    val teamKick: BaseSubCommand = KickTeamCmd()

}