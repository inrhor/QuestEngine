package cn.inrhor.questengine.api.event.data

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class StorageEvent {

    class Set(val player: Player, val key: String, val value: String): BukkitProxyEvent()

    class Remove(val player: Player, val key: String): BukkitProxyEvent()

}