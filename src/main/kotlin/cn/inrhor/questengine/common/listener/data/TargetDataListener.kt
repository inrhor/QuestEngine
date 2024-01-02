package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.TargetDataEvent
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object TargetDataListener {

    @SubscribeEvent
    fun onAddProgress(ev: TargetDataEvent.AddProgress) {
        val targetData = ev.targetData
        targetData.schedule += ev.addProgress
        val p = ev.player
        Database.database.updateTarget(p, targetData, "schedule", targetData.schedule)
    }

    @SubscribeEvent
    fun onSetProgress(ev: TargetDataEvent.SetProgress) {
        val targetData = ev.targetData
        targetData.schedule = ev.setProgress
        val p = ev.player
        Database.database.updateTarget(p, targetData, "schedule", targetData.schedule)
    }

    @SubscribeEvent
    fun onSetState(ev: TargetDataEvent.SetState) {
        val targetData = ev.targetData
        Database.database.updateTarget(ev.player, targetData, "state", targetData.state)
    }

}