package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamAsks {

    val asks = subCommand {
        literal("open") {
            dynamic {
                execute<Player> { sender, _, _ ->
                    val pUUID = sender.uniqueId
                    val tData = TeamManager.getTeamData(pUUID)?: return@execute run {
                        sender.sendLang("TEAM.NO_TEAM") }
                    if (!TeamManager.isLeader(pUUID, tData)) return@execute run {
                        sender.sendLang("TEAM.NOT_LEADER") }
                    HasTeam.openAsks(sender)
                }
            }
        }
        literal("agree") {
            dynamic {
                execute<Player> { sender, context, _ ->
                    val args = context.arguments()
                    manager(sender, args)
                    return@execute
                }
            }
        }
        literal("reject") {
            dynamic {
                execute<Player> { sender, context, _ ->
                    val args = context.arguments()
                    manager(sender, args, false)
                    return@execute
                }
            }
        }
    }

    private fun manager(player: Player, args: Array<String>, agree: Boolean = true) {
        val pUUID = player.uniqueId
        val tData = TeamManager.getTeamData(pUUID)?: return run {
            player.sendLang("TEAM.NO_TEAM") }
        if (!TeamManager.isLeader(pUUID, tData)) return run {
            player.sendLang("TEAM.NOT_LEADER") }
        val mName = args[0]
        val m = Bukkit.getPlayer(mName)?: return run {
            player.sendLang("PLAYER_NOT_ONLINE") }
        val mUUID = m.uniqueId
        TeamManager.removeAsk(mUUID, tData)
        if (agree) {
            TeamManager.addMember(mUUID, tData)
        }
    }

}