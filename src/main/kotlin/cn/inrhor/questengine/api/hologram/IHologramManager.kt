package cn.inrhor.questengine.api.hologram

import cn.inrhor.questengine.common.hologram.HoloDialog


class IHologramManager {
    companion object {
        private val iHologramMap = mutableMapOf<String, HoloDialog>()

        private val holoEntityIDs: MutableSet<Int> = mutableSetOf()
    }

    fun existHoloEntityID(holoEntityID: Int): Boolean {
        return holoEntityIDs.contains(holoEntityID)
    }

    fun addHoloEntityID(holoEntityID: Int) {
        holoEntityIDs.add(holoEntityID)
    }

    /**
     * 获取IHolo实例
     */
    fun getHolo(holoID: String): HoloDialog? {
        return iHologramMap[holoID]!!
    }

    fun addHolo(holoID: String, holo: HoloDialog) {
        iHologramMap[holoID] = holo
    }
}