package cn.inrhor.questengine.common.dialog.chat

import org.bukkit.entity.Player
import java.util.*

class ChatData(
    val uuid: UUID,
    var chatReceive: Boolean,
    var dialogReceive: Boolean,
    var chatContent: MutableList<String>
) {
    constructor(uuid: UUID):
            this(uuid, true, false, mutableListOf())
}