package cn.inrhor.questengine.api.hologram

import cn.inrhor.questengine.common.nms.NMS
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object HoloDisplay {

    fun spawnAS(holoID: Int, viewers: MutableSet<Player>, loc: Location) {
        getPackets().spawnAS(viewers, holoID, loc)
    }

    fun delEntity(holoID: Int, viewers: MutableSet<Player>) {
        getPackets().destroyEntity(viewers, holoID)
    }

    fun delEntity(holoID: Int, viewer: Player) {
        getPackets().destroyEntity(viewer, holoID)
    }

    fun updateText(holoID: Int, viewers: MutableSet<Player>, text: String) {
        getPackets().updateDisplayName(viewers, holoID, text)
    }

    fun updateItem(holoID: Int, itemID: Int, viewers: MutableSet<Player>, loc: Location, item: ItemStack) {
        getPackets().spawnItem(viewers, itemID, loc, item)
        getPackets().updatePassengers(viewers, holoID, itemID)
    }

    fun initTextAS(holoID: Int, viewers: MutableSet<Player>) {
        getPackets().initAS(viewers, holoID, showName = true, isSmall = true, marker = true)
        updateText(holoID, viewers, "")
    }

    fun initItemAS(holoID: Int, viewers: MutableSet<Player>) {
        getPackets().initAS(viewers, holoID, showName = false, isSmall = false, marker = false)
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }

}