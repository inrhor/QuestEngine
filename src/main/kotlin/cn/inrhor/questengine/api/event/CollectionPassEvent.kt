package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.common.database.data.PacketData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class CollectionPassEvent(val player: Player, val packetData: PacketData, val type: PacketEntityInteractEvent.Type): BukkitProxyEvent()