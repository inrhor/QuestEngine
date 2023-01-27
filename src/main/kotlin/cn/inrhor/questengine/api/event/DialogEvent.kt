package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.api.dialog.DialogModule
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class DialogEvent(val player: Player, val dialogModule: DialogModule): BukkitProxyEvent()