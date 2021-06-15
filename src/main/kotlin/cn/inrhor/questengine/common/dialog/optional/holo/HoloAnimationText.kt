package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * 一行一个动态调度器
 */
class HoloAnimationText(var viewers: MutableSet<Player>, val textDialogPlay: TextDialogPlay) {

    fun runTask() {
        var line = 0

        object : BukkitRunnable() {
            override fun run() {
                if (viewers.isEmpty()) cancel()
                val texts = textDialogPlay.texts
                if (line >= texts.size) cancel()
                HoloDisplay.updateText(textDialogPlay.holoID, viewers, textDialogPlay.texts[line])
                line++
            }
        }.runTaskTimer(QuestEngine.plugin, textDialogPlay.startTime.toLong(), 1L)

    }

}