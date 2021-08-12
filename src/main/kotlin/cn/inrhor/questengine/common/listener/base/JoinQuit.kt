package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.SubscribeEvent
import taboolib.common.platform.submit

object JoinQuit {

    @SubscribeEvent
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        val p = ev.player
        val uuid = p.uniqueId
        submit(async = true, delay = 20L) {
            QuestManager.autoQuestMap.forEach { (questID, questModule) ->
                if (QuestManager.existQuestData(p, questID)) {
                    val qData = QuestManager.getQuestData(uuid, questID)
                    if (qData !=null && qData.state == QuestState.FAILURE) {
                        QuestManager.acceptQuest(p, questID)
                        return@submit
                    }
                }
                QuestManager.acceptQuest(p, questID)
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