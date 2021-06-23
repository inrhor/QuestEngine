package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class HoloAnimationItem(var viewers: MutableSet<Player>,
                        val itemDialogPlay: ItemDialogPlay,
                        val holoLoc: Location) {

    fun run() {
        object : BukkitRunnable() {
            override fun run() {
                HoloDisplay.updateItem(itemDialogPlay.holoID, itemDialogPlay.itemID, viewers, holoLoc, itemDialogPlay.item)
            }
        }.runTaskLaterAsynchronously(QuestEngine.plugin, itemDialogPlay.delay.toLong())
    }

}