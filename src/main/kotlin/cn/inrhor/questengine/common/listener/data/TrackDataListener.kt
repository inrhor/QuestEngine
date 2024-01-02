package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.data.TrackDataEvent
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object TrackDataListener {

    @SubscribeEvent
    fun onRemove(ev: TrackDataEvent.Remove) {
        Database.database.removeTrack(ev.player.uniqueId)
    }

    @SubscribeEvent
    fun onSet(ev: TrackDataEvent.Set) {
        Database.database.setTrack(ev.player.uniqueId, ev.trackData)
    }

}