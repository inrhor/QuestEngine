package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.entity.Player

class ChatDialogAPI {

    fun chatInterceptor(player: Player, chatReceive: Boolean) {
        val pData = DataStorage.playerDataStorage[player.uniqueId]!!
        pData.chatData.chatReceive = chatReceive
    }

    fun getChatReceive(player: Player): Boolean {
        val pData = DataStorage.playerDataStorage[player.uniqueId]!!
        return pData.chatData.chatReceive
    }

    fun setDialogReceive(player: Player, dialogReceive: Boolean) {
        val pData = DataStorage.playerDataStorage[player.uniqueId]!!
        pData.chatData.dialogReceive = dialogReceive
    }

    fun getDialogReceive(player: Player): Boolean {
        val pData = DataStorage.playerDataStorage[player.uniqueId]!!
        return pData.chatData.dialogReceive
    }

}