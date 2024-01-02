package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.NavigationEvent
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object NavigateDataListener {

    @SubscribeEvent
    fun onUpdateState(ev: NavigationEvent.UpdateState) {
        Database.database.setNavigation(ev.player.uniqueId, ev.navData.id, "state", ev.navData.state.int)
    }

}