package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.module.lang.sendLang

object TeamCreate {

    val create = subCommand {
        dynamic {
            execute<ProxyPlayer> { sender, context, _ ->
                val pUUID = sender.uniqueId
                if (TeamManager.hasTeam(pUUID)) return@execute run {
                    sender.sendLang("TEAM.HAS_TEAM") }
                val teamName = context.args[1]
                TeamManager.createTeam(teamName, pUUID)
            }
        }
    }


}