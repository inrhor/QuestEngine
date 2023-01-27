package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.ReplyModule
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class ReplyEvent(val player: Player, val dialogModule: DialogModule, val replyModule: ReplyModule): BukkitProxyEvent()