package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.existQuestData
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.acceptQuest
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.*
import taboolib.common.platform.function.*
import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.PacketSendEvent

object JoinQuit {

    @SubscribeEvent
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        val p = ev.player
        submit(async = true, delay = 20L) {
            QuestManager.autoQuestMap.keys.forEach {
                if (!p.existQuestData(it)) {
                    p.acceptQuest(it)
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(ev: PlayerQuitEvent) {
        val uuid = ev.player.uniqueId
        val pData = uuid.getPlayerData()
        val tData = pData.teamData?: return
        if (TeamManager.isLeader(uuid, tData)) {
            tData.delTeam()
        }else {
            TeamManager.removeMember(uuid, tData)
        }
    }

}