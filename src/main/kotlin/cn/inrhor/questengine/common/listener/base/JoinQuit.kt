package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.*
import taboolib.common.platform.function.*

object JoinQuit {

    @SubscribeEvent
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        val p = ev.player
        val uuid = p.uniqueId
        submit(async = true, delay = 20L) {
            QuestManager.autoQuestMap.keys.forEach {
                if (QuestManager.existQuestData(uuid, it, QuestState.DOING) ||
                    QuestManager.existQuestData(uuid, it, QuestState.IDLE)) return@forEach
                QuestManager.acceptQuest(p, it)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(ev: PlayerQuitEvent) {
        val uuid = ev.player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val tData = pData.teamData?: return
        if (TeamManager.isLeader(uuid, tData)) {
            tData.delTeam()
        }else {
            TeamManager.removeMember(uuid, tData)
        }
    }

}