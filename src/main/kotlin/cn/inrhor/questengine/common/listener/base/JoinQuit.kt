package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.PlayerData
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@TListener
class JoinQuit: Listener {

    @EventHandler
    fun onPlayerQuit(ev: PlayerQuitEvent) {
        val uuid = ev.player.uniqueId
        val pData = PlayerData(uuid)
        val tData = pData.teamData?: return
        if (TeamManager.isLeader(uuid, tData)) {
            tData.delTeam()
        }else {
            TeamManager.removeMember(uuid, tData)
        }
    }

}