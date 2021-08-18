package cn.inrhor.questengine.common.nms

import cn.inrhor.questengine.api.event.PacketEntityInteractEvent
import cn.inrhor.questengine.common.database.data.PacketData
import cn.inrhor.questengine.common.packet.PacketManager
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent

object PacketNMSListener {

    @SubscribeEvent
    fun click(ev: PacketReceiveEvent) {
        if (ev.packet.name == "PacketPlayInUseEntity") {
            val p = ev.player
            val packetData = PacketManager.getPacketData(p, ev.packet.read("a")!!)?: return
            if (MinecraftVersion.isUniversal) {
                val action = ev.packet.read<Any>("action")!!
                click(action.javaClass.simpleName, p, packetData)
            }else {
                click(ev.packet.read<Any>("action").toString(), p, packetData)
            }
        }
    }

    private fun click(str: String, player: Player, packetData: PacketData) {
        when (str) {
            "d", "ATTACK" -> {
                submit {
                    PacketEntityInteractEvent(player, packetData, PacketEntityInteractEvent.Type.LEFT)
                }
            }
            "e", "INTERACT_AT" -> {
                submit {
                    PacketEntityInteractEvent(player, packetData, PacketEntityInteractEvent.Type.RIGHT)
                }
            }
        }
    }

}