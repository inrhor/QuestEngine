package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import cn.inrhor.questengine.common.collaboration.ui.chat.NoTeam
import org.bukkit.entity.Player
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand

object TeamOpen {

    @CommandBody
    val open = subCommand {
        literal("team") {
            literal("open") {
                dynamic {
                    execute<ProxyPlayer> { sender, _, _ ->
                        val pUUID = sender.uniqueId
                        val player = sender as Player
                        if (TeamManager.hasTeam(pUUID)) {
                            HasTeam.openInfo(player)
                            return@execute
                        }
                        NoTeam.openHome(player)
                    }
                }
            }
        }
    }


}