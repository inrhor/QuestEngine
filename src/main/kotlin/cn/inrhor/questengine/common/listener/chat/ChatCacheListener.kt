package cn.inrhor.questengine.common.listener.chat

import cn.inrhor.questengine.common.database.data.DataStorage
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent

object ChatCacheListener {

    @SubscribeEvent
    fun prevent(ev: PacketSendEvent) {
        if (ev.packet.name == "PacketPlayOutChat" && ev.packet.read<Any>("b").toString() != "GAME_INFO") {
            var a = ev.packet.read<Any>("a").toString()
            if (a == "null") {
                if (MinecraftVersion.major >= 10 || MinecraftVersion.majorLegacy < 11700) {
                    kotlin.runCatching {
                        a = Coerce.toList(ev.packet.read<Any>("components")).toString()
                    }
                } else return
            }
            val p = ev.player
            val pData = DataStorage.getPlayerData(p)
            val cache = pData.chatCache
            if (!cache.enable) return
            if (cache.release.contains(a) || a.contains("@d31877bc-b8bc-4355-a4e5-9b055a494e9f")) return
            cache.append(ev.packet.source)
            ev.isCancelled = true
        }
    }

}