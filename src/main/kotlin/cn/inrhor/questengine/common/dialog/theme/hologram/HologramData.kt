package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.hologram.HoloIDManager

/**
 * 全息数据
 */
class HologramData {

    /**
     * 整数型ID存储
     */
    val packetIDs = mutableSetOf<Int>()

    fun create(packetID: Int) {
        HoloIDManager.addEntityID(packetID)
    }

    fun remove() {

    }

    fun clear() {

    }

    fun size(): Int {
        return packetIDs.size
    }

}