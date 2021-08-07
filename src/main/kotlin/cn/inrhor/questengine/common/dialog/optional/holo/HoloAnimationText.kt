package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.spawnAS
import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.submit

/**
 * 一行一个动态调度器
 */
class HoloAnimationText(val holoDialog: HoloDialog,
                        var viewers: MutableSet<Player>,
                        val textDialogPlay: TextDialogPlay,
                        val holoLoc: Location
) {

    fun runTask() {
        var line = 0

        val holoID = textDialogPlay.holoID

        spawnAS(viewers, holoID, holoLoc)
        HoloDisplay.initTextAS(holoID, viewers)

        submit(async = true, delay = textDialogPlay.startTime.toLong(), period = 1L) {
            val texts = textDialogPlay.texts
            if (holoDialog.endDialog || viewers.isEmpty() || line >= texts.size) {
                cancel(); return@submit
            }
            HoloDisplay.updateText(holoID, viewers, texts[line])
            line++
        }

    }

}