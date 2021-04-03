package cn.inrhor.questengine.common.listener.base

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@TListener
class JoinQuitListener: Listener {

    @EventHandler
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        val uuid = ev.player.uniqueId
        val playerData = PlayerData(uuid)
        DataStorage.playerDataStorage[uuid] = playerData
    }

}