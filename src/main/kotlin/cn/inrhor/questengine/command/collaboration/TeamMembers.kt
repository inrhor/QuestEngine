package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import org.bukkit.entity.Player
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamMembers {

    val members = subCommand {
        execute<Player> { sender, _, _ ->
            val pUUID = sender.uniqueId
            if (!TeamManager.hasTeam(pUUID)) return@execute run {
                sender.sendLang("TEAM-NO_TEAM") }
            HasTeam.openMembers(sender)
        }
    }


}