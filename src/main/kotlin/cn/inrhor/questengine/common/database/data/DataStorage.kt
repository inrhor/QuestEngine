package cn.inrhor.questengine.common.database.data

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DataStorage {
    companion object {
        val playerDataStorage = ConcurrentHashMap<UUID, PlayerData>()
    }

    fun getPlayerData(player: Player): PlayerData {
        return DataStorage.playerDataStorage[player.uniqueId]!!
    }
}