package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.api.packet.destroyEntity
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

    fun addID(packetID: Int) {
        packetIDs.add(packetID)
        HoloIDManager.addEntityID(packetID)
    }

    fun create(packetID: Int, viewers: MutableSet<Player>, originLocation: OriginLocation, type: HoloIDManager.Type) {
        addID(packetID)
        spawnAS(viewers, packetID, originLocation.origin)
        if (type == HoloIDManager.Type.ITEM) {
            HoloDisplay.initItemAS(packetID, viewers)
        }else {
            HoloDisplay.initTextAS(packetID, viewers)
        }
    }

    fun remove(player: Player) {
        remove(mutableSetOf(player))
    }

    fun remove(viewers: MutableSet<Player>) {
        packetIDs.forEach {
            destroyEntity(viewers, it)
        }
    }

    fun size(): Int {
        return packetIDs.size
    }

}