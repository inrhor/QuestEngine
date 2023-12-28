package cn.inrhor.questengine.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class TagEvent {

    class AddTag(val player: Player, val tag: String): BukkitProxyEvent()

    class RemoveTag(val player: Player, val tag: String): BukkitProxyEvent()

    class ClearTag(val player: Player): BukkitProxyEvent()

}