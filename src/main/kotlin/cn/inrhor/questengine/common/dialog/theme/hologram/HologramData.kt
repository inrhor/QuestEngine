package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.api.packet.spawnAS
import org.bukkit.entity.Player

/**
 * 全息数据
 */
class HologramData {

    /**
     * 整数型ID存储
     */
    val packetIDs = mutableSetOf<Int>()

    fun create(packetID: Int, viewers: MutableSet<Player>, originLocation: OriginLocation) {
        HoloIDManager.addEntityID(packetID)
        spawnAS(viewers, packetID, originLocation.origin)
        HoloDisplay.initTextAS(packetID, viewers)
    }

    fun remove() {

    }

    fun clear() {

    }

    fun size(): Int {
        return packetIDs.size
    }

}