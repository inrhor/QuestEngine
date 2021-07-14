package cn.inrhor.questengine.command.collaboration

import io.izzel.taboolib.module.command.base.BaseMainCommand

class TeamCommand: BaseMainCommand() {

    val create = CreateTeamCmd()

    val invite = InviteTeamCmd()

    val join = JoinTeamCmd()

    val kick = KickTeamCmd()

    val del = DelTeamCmd()



}