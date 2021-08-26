package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.*

class HoloAnimationItem(val holoDialog: HoloDialog,
                        var viewers: MutableSet<Player>,
                        val itemDialogPlay: ItemDialogPlay,
                        val holoLoc: Location) {

    fun run() {
        submit(delay = itemDialogPlay.delay.toLong(), async = true) {
            if (holoDialog.endDialog || viewers.isEmpty()) { cancel(); return@submit }
            if (itemDialogPlay.type == ItemDialogPlay.Type.SUSPEND) {
                HoloDisplay.updateItem(
                    itemDialogPlay.holoID,
                    itemDialogPlay.itemID,
                    viewers,
                    holoLoc,
                    itemDialogPlay.item)
            }else {
                HoloDisplay.equipHeadItem(
                    itemDialogPlay.holoID,
                    viewers,
                    itemDialogPlay.item)
            }
        }
    }

}