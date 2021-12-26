package cn.inrhor.questengine.common.dialog.theme.hologram.content

import cn.inrhor.questengine.api.dialog.theme.ItemPlay
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.dialog.theme.hologram.HologramData
import cn.inrhor.questengine.common.dialog.theme.hologram.OriginLocation
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.spaceSplit
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * 全息物品
 */
class AnimationItem(val content: String, val holoData: HologramData): ItemPlay() {

    init {
        content.variableReader().forEach {
            if (it.startsWith("delay ")) {
                delay = it.spaceSplit(1).toLong()
            }else if (it.startsWith("use ")) {
                val type = it.spaceSplit(1).uppercase()
                displayType = Type.valueOf(type)
            }else if (it.startsWith("item ")) {
                itemStack = ItemManager.get(it.spaceSplit(1))
            }
        }
    }

    fun sendViewers(viewers: MutableSet<Player>, origin: OriginLocation, vararg holoID: Int) {
        submit(async = true, delay = this.delay) {
            if (viewers.isEmpty()) {
                cancel(); return@submit
            }
            val itemHoloID = holoID[0]
            if (displayType == Type.SUSPEND) {
                val stackID = holoID[1]
                HoloDisplay.passengerItem(itemHoloID, stackID, viewers, origin.origin, itemStack)
                holoData.addID(stackID)
                return@submit
            }
            HoloDisplay.equipHeadItem(itemHoloID, viewers, itemStack)
        }
    }

}