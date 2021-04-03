package cn.inrhor.questengine.common.listener

import cn.inrhor.questengine.api.dialog.ChatDialogAPI
import cn.inrhor.questengine.utlis.public.MsgUtil
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import org.bukkit.entity.Player
import org.bukkit.event.Listener

@TListener
class ChatListener: Listener {

    @TPacket(type = TPacket.Type.SEND)
    fun chatChecker(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayOutChat") ) {
            if (!ChatDialogAPI().getChatReceive(player)) {
                MsgUtil.send("dialog  "+ChatDialogAPI().getDialogReceive(player))
                return ChatDialogAPI().getDialogReceive(player)
            }
        }
        return true
    }

}