package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.dialog.chat.ChatData
import java.util.*

class PlayerData(
    val uuid: UUID,
    val chatData: ChatData,
    var dialogState: Boolean,
    var mainQuestList: MutableList<String>,
    var controlList: MutableList<String>) {

    constructor(uuid: UUID):
            this(uuid, ChatData(uuid), false, mutableListOf(), mutableListOf())
}