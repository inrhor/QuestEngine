package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.spawnAS
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class HoloReplyDisplay {

    fun text(holoID: Int, viewers: MutableSet<Player>, holoLoc: Location, text: String) {
        spawnAS(viewers, holoID, holoLoc)
        HoloDisplay.initTextAS(holoID, viewers)
        HoloDisplay.updateText(holoID, viewers, text)
    }

    fun item(holoID: Int, itemID: Int, viewers: MutableSet<Player>, holoLoc: Location, item: ItemStack) {
        spawnAS(viewers, holoID, holoLoc)
        HoloDisplay.initItemAS(holoID, viewers)
        HoloDisplay.updateItem(holoID, itemID, viewers, holoLoc, item)
    }
}