package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.common.database.data.DialogData
import cn.inrhor.questengine.common.dialog.theme.hologram.core.HoloHitBox
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class HoloClickEvent(val player: Player, val dialogData: DialogData, val holoHitBox: HoloHitBox): BukkitProxyEvent() {
}