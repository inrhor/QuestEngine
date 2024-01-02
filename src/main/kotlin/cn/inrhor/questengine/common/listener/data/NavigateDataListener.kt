package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.data.NavigationDataEvent
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object NavigateDataListener {

    @SubscribeEvent
    fun onUpdateState(ev: NavigationDataEvent.UpdateState) {
        Database.database.setNavigation(ev.player.uniqueId, ev.navData.id, "state", ev.navData.state.int)
    }

    @SubscribeEvent
    fun onRemove(ev: NavigationDataEvent.Remove) {
        Database.database.removeNavigation(ev.player.uniqueId, ev.navData.id)
    }

}