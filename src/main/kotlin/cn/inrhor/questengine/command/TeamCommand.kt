package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.collaboration.*
import taboolib.common.platform.CommandBody

object TeamCommand {

    @CommandBody
    val open = TeamOpen

    @CommandBody
    val create = TeamCreate

    @CommandBody
    val join = TeamJoin

    @CommandBody
    val invite = TeamInvite

    @CommandBody
    val members = TeamMembers

    @CommandBody
    val asks = TeamAsks

    @CommandBody
    val leave = TeamLeave

    @CommandBody
    val delete = TeamDelete

    @CommandBody
    val kick = TeamKick

}