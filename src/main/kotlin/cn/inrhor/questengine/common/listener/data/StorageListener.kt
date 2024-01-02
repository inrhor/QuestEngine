package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.StorageEvent
import cn.inrhor.questengine.api.manager.DataManager.storage
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object StorageListener {

    @SubscribeEvent
    fun onSet(ev: StorageEvent.Set) {
        val player = ev.player
        val key = ev.key
        val value = ev.value
        player.storage()[key] = value
        Database.database.setStorage(player.uniqueId, key, value)
    }

    @SubscribeEvent
    fun onRemove(ev: StorageEvent.Remove) {
        val player = ev.player
        val key = ev.key
        player.storage().remove(key)
        Database.database.removeStorage(player.uniqueId, key)
    }

}