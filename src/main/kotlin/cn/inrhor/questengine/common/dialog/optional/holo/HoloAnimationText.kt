package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.*

/**
 * 一行一个动态调度器
 */
class HoloAnimationText(val holoDialog: HoloDialog,
                        var viewer: Player,
                        val textDialogPlay: TextDialogPlay,
                        val holoLoc: Location
) {

    fun runTask() {
        var line = 0

        val holoID = textDialogPlay.holoID

        spawnAS(mutableSetOf(viewer), holoID, holoLoc)
        HoloDisplay.initTextAS(holoID, viewer)

        submit(async = true, delay = textDialogPlay.startTime.toLong(), period = 1L) {
            val texts = textDialogPlay.texts
            if (holoDialog.endDialog || !viewer.isOnline) {
                cancel(); return@submit
            }
            if (line >= texts.size) {
                textDialogPlay.sendChat.forEach {
                    viewer.sendMessage(it)
                }
                cancel(); return@submit
            }
            HoloDisplay.updateText(holoID, viewer, texts[line])
            line++
        }

    }

}