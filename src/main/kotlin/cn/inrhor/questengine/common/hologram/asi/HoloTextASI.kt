package cn.inrhor.questengine.common.hologram.asi

import cn.inrhor.questengine.common.dialog.animation.text.TagText
import cn.inrhor.questengine.common.nms.NMS
import org.bukkit.Location
import org.bukkit.entity.Player

class HoloTextASI(
    val entityID: Int,
    val viewers: MutableSet<Player>,
    val tagText: MutableList<TagText>,
    val location: Location,
    var index: Int
) {

    fun send() {
        getPackets().spawnAS(viewers, entityID, location)
        updateContent(index)
    }

    fun updateContent(frame: Int) {
        getPackets().updateDisplayName(viewers, entityID, )
    }

    fun remove() {
        getPackets().destroyEntity(viewers, entityID)
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }
}