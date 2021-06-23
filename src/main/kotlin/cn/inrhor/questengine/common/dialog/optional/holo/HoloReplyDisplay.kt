package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.hologram.HoloDisplay
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class HoloReplyDisplay {

    fun text(holoID: Int, viewers: MutableSet<Player>, holoLoc: Location, text: String) {
        HoloDisplay.spawnAS(holoID, viewers, holoLoc)
        HoloDisplay.initTextAS(holoID, viewers)
        HoloDisplay.updateText(holoID, viewers, text)
    }

    fun item(holoID: Int, itemID: Int, viewers: MutableSet<Player>, holoLoc: Location, item: ItemStack) {
        HoloDisplay.spawnAS(holoID, viewers, holoLoc)
        HoloDisplay.initItemAS(holoID, viewers)
        HoloDisplay.updateItem(holoID, itemID, viewers, holoLoc, item)
    }
}