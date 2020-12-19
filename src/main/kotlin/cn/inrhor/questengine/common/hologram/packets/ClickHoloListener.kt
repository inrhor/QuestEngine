package cn.inrhor.questengine.common.hologram.packets


import cn.inrhor.questengine.QuestEngine
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers

class ClickHoloListener : PacketAdapter(
    QuestEngine.plugin,
    ListenerPriority.NORMAL,
    PacketType.Play.Client.USE_ENTITY) {
    fun click() {
        /*ProtocolLibrary.getProtocolManager().addPacketListener(object :
            PacketAdapter(QuestEngine.plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.USE_ENTITY) {
            override fun onPacketReceiving(ev: PacketEvent) {
                val packet = ev.packet
                packet.integers.read(0)
                if (packet.entityUseActions.values[0] != EnumWrappers.EntityUseAction.INTERACT) return
                if (!PacketHolo().isHoloPacket(packet.integers.values[0])) return
                // run script
            }
        })*/
    }

    override fun onPacketReceiving(ev: PacketEvent) {
        val packet = ev.packet
        packet.integers.read(0)
        if (packet.entityUseActions.values[0] != EnumWrappers.EntityUseAction.INTERACT) return
//        if (!PacketHolo().isHoloPacket(packet.integers.values[0])) return
        // run script
    }
}