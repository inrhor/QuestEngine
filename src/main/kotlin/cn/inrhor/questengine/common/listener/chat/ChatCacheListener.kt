package cn.inrhor.questengine.common.listener.chat

import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.SubscribeEvent

object ChatCacheListener {

    @SubscribeEvent
    fun prevent(ev: AsyncPlayerChatEvent) {
        val p = ev.player
        val pData = DataStorage.getPlayerData(p)
        val cache = pData.chatCache
        if (!cache.enable) return
        cache.addMessage(ev.message)
    }

}