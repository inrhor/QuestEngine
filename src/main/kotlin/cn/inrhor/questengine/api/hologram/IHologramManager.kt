package cn.inrhor.questengine.api.hologram

import cn.inrhor.questengine.common.hologram.IHolo


public class IHologramManager {
    private val iHologramMap = mutableMapOf<String, IHolo>()

    private val holoEntityIDs: MutableSet<Int> = mutableSetOf()

    fun existHoloEntityID(holoEntityID: Int): Boolean {
        return holoEntityIDs.contains(holoEntityID)
    }

    fun addHoloEntityID(holoEntityID: Int) {
        holoEntityIDs.add(holoEntityID)
    }

    /**
     * 获取IHolo实例
     */
    fun getHolo(holoID: String): IHolo? {
        return iHologramMap[holoID]!!
    }

    fun addHolo(holoID: String, holo: IHolo) {
        iHologramMap[holoID] = holo
    }
}