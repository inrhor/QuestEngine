package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.DialogTheme
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * 全息对话
 */
class DialogHologram(
    val dialogModule: DialogModule,
    val viewers: MutableSet<Player>,
    val location: Location): DialogTheme() {

    /**
     * 向可视者播放全息对话
     */
    override fun play() {
        val origin = OriginLocation(location)
        content(origin)
    }

    private fun content(origin: OriginLocation) {
        dialogModule.dialog.forEach {
            val u = it.lowercase()
            if (u.startsWith("initloc ")) {
                origin.reset(it)
            }else if (u.startsWith("addloc ")) {
                origin.add(it)
            }
        }
    }

}