package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.PlayerData
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.SubscribeEvent

object JoinQuit {

    @SubscribeEvent
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