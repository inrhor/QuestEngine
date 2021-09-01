package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.compat.replacePlaceholder

class HoloReplyDisplay {

    fun text(holoID: Int, viewers: MutableSet<Player>, holoLoc: Location, text: String) {
        spawnAS(viewers, holoID, holoLoc)
        viewers.forEach {
            HoloDisplay.initTextAS(holoID, it)
            HoloDisplay.updateText(holoID, it, text.replacePlaceholder(it))
        }
    }

    fun item(holoID: Int, itemID: Int, viewers: MutableSet<Player>, holoLoc: Location, item: ItemStack, type: ItemDialogPlay.Type) {
        spawnAS(viewers, holoID, holoLoc)
        HoloDisplay.initItemAS(holoID, viewers)
        if (type == ItemDialogPlay.Type.SUSPEND) {
            HoloDisplay.updateItem(holoID, itemID, viewers, holoLoc, item)
        }else {
            HoloDisplay.equipHeadItem(holoID, viewers, item)
        }
    }
}