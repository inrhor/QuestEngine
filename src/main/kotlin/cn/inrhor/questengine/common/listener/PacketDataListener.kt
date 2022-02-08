package cn.inrhor.questengine.common.listener

import cn.inrhor.questengine.api.event.CollectionPassEvent
import cn.inrhor.questengine.api.event.PacketEntityInteractEvent
import cn.inrhor.questengine.common.packet.PacketManager
import cn.inrhor.questengine.common.packet.RatioDisplay
import cn.inrhor.questengine.script.kether.runEval
import taboolib.common.platform.event.SubscribeEvent

object PacketDataListener {

    @SubscribeEvent
    fun click(ev: PacketEntityInteractEvent) {
        val packetData = ev.packetData
        val packetModule = packetData.packetModule
        val action = packetModule.action?: return
        if (!ev.pass()) return
        val p = ev.player
        RatioDisplay.appear(p, packetData)
        action.trigger.forEach {
            runEval(p, it)
        }
        if (packetData.clickAction.passClickCount()) {
            CollectionPassEvent(p, packetData, ev.type).call()
            action.pass.forEach {
                if (it.lowercase() == "remove") {
                   PacketManager.removePacketEntity(p, packetModule.packedID, packetData.entityID)
                }else {
                    runEval(p, it)
                }
            }
        }
    }

}