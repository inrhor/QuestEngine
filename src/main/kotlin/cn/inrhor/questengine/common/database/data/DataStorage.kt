package cn.inrhor.questengine.common.database.data

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DataStorage {
    companion object {
        private val playerDataStorage = ConcurrentHashMap<UUID, PlayerData>()
    }

    fun addPlayerData(uuid: UUID, playerData: PlayerData) {
        playerDataStorage[uuid] = playerData
    }

    fun getPlayerData(player: Player): PlayerData? {
        return playerDataStorage[player.uniqueId]
    }

    fun getPlayerData(uuid: UUID): PlayerData? {
        return playerDataStorage[uuid]
    }
}