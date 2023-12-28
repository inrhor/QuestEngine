package cn.inrhor.questengine.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class TagEvent {

    class Add(val player: Player, val tag: String): BukkitProxyEvent()

    class Remove(val player: Player, val tag: String): BukkitProxyEvent()

    class Clear(val player: Player): BukkitProxyEvent()

}