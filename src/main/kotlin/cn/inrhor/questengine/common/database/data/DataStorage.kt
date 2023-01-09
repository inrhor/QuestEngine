package cn.inrhor.questengine.common.database.data

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object DataStorage {
    private val playerDataStorage = ConcurrentHashMap<UUID, PlayerData>()

    fun removePlayerData(uuid: UUID) {
        playerDataStorage.remove(uuid)
    }

    fun addPlayerData(uuid: UUID, playerData: PlayerData) {
        playerDataStorage[uuid] = playerData
    }

    fun Player.getPlayerData(): PlayerData {
        return this.uniqueId.getPlayerData()
    }

    fun UUID.getPlayerData(): PlayerData {
        var pData = playerDataStorage[this]
        if (pData == null) {
            pData = PlayerData(this)
            Bukkit.getPlayer(this)?.sendLang("DATA-NULL_DATA")
        }
        return pData
    }
}