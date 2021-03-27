package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.QuestEngine
import me.tom.sparse.spigot.chat.protocol.ChatPacketInterceptor
import me.tom.sparse.spigot.chat.protocol.PlayerChatIntercept
import org.bukkit.entity.Player

class ChatAPI {
    companion object {
        private lateinit var interceptor: ChatPacketInterceptor
    }

    fun init() {
        interceptor = ChatPacketInterceptor(QuestEngine.plugin)
    }

    fun playerChatInterceptor(player: Player): PlayerChatIntercept {
        return interceptor.getChat(player)
    }


}