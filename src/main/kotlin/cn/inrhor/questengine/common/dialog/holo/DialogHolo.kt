package cn.inrhor.questengine.common.dialog.holo

import cn.inrhor.questengine.QuestEngine
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class DialogHolo(
    val viewers: MutableList<Player>,
    var runnable: BukkitRunnable?
) {

    constructor(viewers: MutableList<Player>) : this(viewers, null)

    @TSchedule
    fun runRunnable() {
        var i = 0
        runnable = object : BukkitRunnable() {
            override fun run() {
                if (viewers.isEmpty()) {
                    cancel()
                    return
                }

                i++
            }
        }
        (runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0L, 1L)
    }
}