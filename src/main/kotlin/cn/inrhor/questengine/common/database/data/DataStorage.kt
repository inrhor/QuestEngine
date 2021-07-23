package cn.inrhor.questengine.common.database.data

import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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

    fun getPlayerData(player: Player): PlayerData {
        return getPlayerData(player.uniqueId)
    }

    fun getPlayerData(uuid: UUID): PlayerData {
        var pData = playerDataStorage[uuid]
        if (pData == null) {
            pData = PlayerData(uuid)
            TLocale.sendTo(Bukkit.getPlayer(uuid)!!, "DATA.NULL_DATA")
        }
        return pData
    }
}