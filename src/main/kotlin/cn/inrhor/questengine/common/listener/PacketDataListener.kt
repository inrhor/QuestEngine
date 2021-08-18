package cn.inrhor.questengine.common.listener

import cn.inrhor.questengine.api.event.PacketEntityInteractEvent
import cn.inrhor.questengine.script.kether.eval
import taboolib.common.platform.event.SubscribeEvent

object PacketDataListener {

    @SubscribeEvent
    fun click(ev: PacketEntityInteractEvent) {
        val packetData = ev.packetData
        val packetModule = packetData.packetModule
        val action = packetModule.action?: return
        if (!ev.pass()) return
        val p = ev.player
        action.trigger.forEach {
            eval(p, it)
        }
        if (packetData.clickAction.passClickCount()) {
            action.pass.forEach {
                eval(p, it)
            }
        }
    }

}