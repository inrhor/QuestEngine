package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

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

        HoloDisplay.spawnAS(holoID, viewers, holoLoc)
        HoloDisplay.initTextAS(holoID, viewers)

        object : BukkitRunnable() {
            override fun run() {
                val texts = textDialogPlay.texts
                if (holoDialog.endDialog || viewers.isEmpty() || line >= texts.size) { cancel(); return }
                HoloDisplay.updateText(holoID, viewers, texts[line])
                line++
            }
        }.runTaskTimerAsynchronously(QuestEngine.plugin, textDialogPlay.startTime.toLong(), 1L)

    }

}