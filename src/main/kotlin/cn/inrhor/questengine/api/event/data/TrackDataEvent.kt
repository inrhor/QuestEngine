package cn.inrhor.questengine.api.event.data

import cn.inrhor.questengine.common.database.data.TrackData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class TrackDataEvent {

    class Set(val player: Player, val trackData: TrackData): BukkitProxyEvent()

    class Remove(val player: Player, val trackData: TrackData): BukkitProxyEvent()

}