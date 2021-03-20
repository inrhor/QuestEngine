package cn.inrhor.questengine.common.database.data

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DataStorage {
    companion object {
        val playerDataStorage = ConcurrentHashMap<UUID, PlayerData>()
    }
}