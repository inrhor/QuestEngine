package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.script.kether.KetherHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.SubscribeEvent

object ClickHolo {

    @SubscribeEvent
    fun clickAction(ev: PlayerInteractEvent) {
        val p = ev.player
        if (ev.action != Action.LEFT_CLICK_AIR) return
        val pData = DataStorage.getPlayerData(p)
        val dialogData = pData.dialogData
        dialogData.holoBoxMap.values.forEach {
            it.forEach{ holoBox ->
                if (holoBox.isBox()) {
                    val replyModule = holoBox.replyModule
                    for (script in replyModule.script) {
                        KetherHandler.eval(p, script)
                    }
                    for (viewer in holoBox.viewers) {
                        val data = DataStorage.getPlayerData(viewer)
                        data.dialogData.endHoloDialog(holoBox.replyModule.dialogID)
                    }
                    return
                }
            }
        }
    }

}