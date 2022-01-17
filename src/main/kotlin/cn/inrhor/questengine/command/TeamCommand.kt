package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.collaboration.*
import taboolib.common.platform.command.*

internal object TeamCommand {

    @CommandBody
    val open = TeamOpen.open

    @CommandBody
    val create = TeamCreate.create

    @CommandBody
    val join = TeamJoin.join

    @CommandBody
    val invite = TeamInvite.invite

    @CommandBody
    val members = TeamMembers.members

    @CommandBody
    val asks = TeamAsks.asks

    @CommandBody
    val leave = TeamLeave.leave

    @CommandBody
    val delete = TeamDelete.delete

    @CommandBody
    val kick = TeamKick.kick

}