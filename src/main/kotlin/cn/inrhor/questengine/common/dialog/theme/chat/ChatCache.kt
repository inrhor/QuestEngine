package cn.inrhor.questengine.common.dialog.theme.chat

import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common5.Coerce
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket

class ChatCache(var enable: Boolean = false, val cache: MutableList<Any> = mutableListOf(), val release: MutableList<String> = mutableListOf()) {

    fun open() {
        enable = true
    }

    fun append(any: Any) {
        if (cache.size > 32) {
            cache.removeAt(0)
        }
        cache.add(any)
    }

    fun close(player: Player, noCacheChat: Boolean) {
        enable = false
        if (!noCacheChat) {
            cache.forEach {
                var value = it.getProperty<Any>("a").toString()
                if (value == "null" && MinecraftVersion.majorLegacy < 11700) {
                    kotlin.runCatching { value = Coerce.toList(it.getProperty<Any>("components")).toString() }
                }
                release.add(value)
                player.sendPacket(it)
            }
        }
        cache.clear()
        release.clear()
    }

}