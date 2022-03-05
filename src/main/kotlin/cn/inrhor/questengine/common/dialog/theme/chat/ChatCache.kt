package cn.inrhor.questengine.common.dialog.theme.chat

import org.bukkit.entity.Player

class ChatCache(var enable: Boolean = false, val msg: MutableList<String> = mutableListOf()) {

    fun open() {
        enable = true
        msg.clear()
    }

    fun addMessage(str: String) {
        if (msg.size > 30) {
            msg.removeAt(0)
        }
        msg.add(str)
    }

    fun close(player: Player) {
        enable = false
        msg.forEach { player.sendMessage(it) }
    }

}