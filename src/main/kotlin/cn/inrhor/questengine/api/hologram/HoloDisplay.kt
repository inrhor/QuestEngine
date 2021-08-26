package cn.inrhor.questengine.api.hologram

import cn.inrhor.questengine.api.packet.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object HoloDisplay {

    fun updateText(holoID: Int, viewers: MutableSet<Player>, text: String) {
        updateDisplayName(viewers, holoID, text)
    }

    fun updateItem(holoID: Int, itemID: Int, viewers: MutableSet<Player>, loc: Location, item: ItemStack) {
        spawnItem(viewers, itemID, loc, item)
        updatePassengers(viewers, holoID, itemID)
    }

    fun equipHeadItem(holoID: Int, viewers: MutableSet<Player>, item: ItemStack) {
        updateEquipmentItem(viewers, holoID, EquipmentSlot.HEAD, item)
    }

    fun initTextAS(holoID: Int, viewers: MutableSet<Player>) {
        initAS(viewers, holoID, showName = true, isSmall = true, marker = true)
        updateText(holoID, viewers, "")
    }

    fun initItemAS(holoID: Int, viewers: MutableSet<Player>) {
        initAS(viewers, holoID, showName = false, isSmall = false, marker = false)
    }

}