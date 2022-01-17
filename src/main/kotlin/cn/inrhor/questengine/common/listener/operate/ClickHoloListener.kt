package cn.inrhor.questengine.common.listener.operate

import cn.inrhor.questengine.api.event.HoloClickEvent
import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.*

object ClickHoloListener {

    @SubscribeEvent
    fun clickAction(ev: PlayerInteractEvent) {
        val p = ev.player
        if (ev.action != Action.LEFT_CLICK_AIR) return
        val pData = DataStorage.getPlayerData(p)
        val dialogData = pData.dialogData
        dialogData.holoBoxMap.values.forEach {
            it.forEach{ holoBox ->
                holoBox.hitBoxList.forEach { b ->
                    if (b.isBox(p)) {
                        HoloClickEvent(p, dialogData, b).call()
                        return
                    }
                }
            }
        }
    }

}