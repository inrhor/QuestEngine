package cn.inrhor.questengine.common.listener.holo

import cn.inrhor.questengine.common.database.data.DataStorage
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

@TListener
class ClickHolo: Listener {

    @EventHandler
    fun clickAction(ev: PlayerInteractEvent) {
        val p = ev.player
        if (ev.action != Action.LEFT_CLICK_AIR) return
        val pData = DataStorage.playerDataStorage[p.uniqueId]?: return
        /*for (clickBox in pData.clickBoxList) {
            if (clickBox.isClick(p)) {
                MsgUtil.send("okClick")
                return
            }
        }*/
    }

}