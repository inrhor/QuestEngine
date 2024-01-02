package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.QuestDataEvent
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.utlis.time.toDate
import taboolib.common.platform.event.SubscribeEvent

object QuestDataListener {

    @SubscribeEvent
    fun onInstall(ev: QuestDataEvent.Install) {
        val player = ev.player
        val questData = ev.questData
        Database.database.createQuest(player.uniqueId, questData)
    }

    @SubscribeEvent
    fun onUnload(ev: QuestDataEvent.Unload) {
        val player = ev.player
        val questId = ev.questId
        Database.database.removeQuest(player.uniqueId, questId)
    }

    @SubscribeEvent
    fun onToggleState(ev: QuestDataEvent.ToggleState) {
        val player = ev.player
        val questData = ev.questData
        Database.database.updateQuest(player.uniqueId, questData.id, "state", questData.state.int)
    }

    @SubscribeEvent
    fun onToggleFinishTime(ev: QuestDataEvent.ToggleFinishTime) {
        val player = ev.player
        val questData = ev.questData
        Database.database.updateQuest(player.uniqueId, questData.id, "end", questData.end.toDate())
    }

}