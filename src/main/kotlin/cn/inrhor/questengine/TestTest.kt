package cn.inrhor.questengine

import cn.inrhor.questengine.api.dialog.ChatAPI
import cn.inrhor.questengine.common.nms.NMS
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent


@TListener
class TestTest : Listener {

    @EventHandler
    fun test(ev: PlayerDropItemEvent) {
        val player = ev.player
//        val menu = ChatMenu()
//        menu.setPauseChat(true)
//        menu.openFor(player)
        ChatAPI().chatInterceptor(player).pause()
    }

    @EventHandler
    fun out(ev: BlockPlaceEvent) {
        val player = ev.player
//        ChatMenuAPI.setCurrentMenu(player, null)
        ChatAPI().chatInterceptor(player).resume()
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }

}