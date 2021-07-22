package cn.inrhor.questengine.common.database

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.type.DatabaseLocal
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.database.type.DatabaseSQL
import cn.inrhor.questengine.common.database.type.DatabaseType
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 为玩家上载数据
     */
    abstract fun push(player: Player)

    @TListener
    companion object : Listener {

        val database: Database by lazy {
            when (DatabaseManager.type) {
                DatabaseType.MYSQL -> DatabaseSQL()
                else -> DatabaseLocal()
            }
        }

        @EventHandler
        fun join(ev: PlayerJoinEvent) {
            val uuid = ev.player.uniqueId
            val pData = PlayerData(uuid)
            DataStorage.addPlayerData(uuid, pData)
            database.pull(ev.player)
        }

        @EventHandler
        fun quit(ev: PlayerQuitEvent) {
            database.push(ev.player)
            val uuid = ev.player.uniqueId
            DataStorage.removePlayerData(uuid)
        }

        @TFunction.Cancel
        private fun cancel() {
            pushAll()
        }

        @TSchedule(period = 100, async = true)
        fun updateDatabase() {
            pushAll()
        }

        private fun pushAll() {
            Bukkit.getOnlinePlayers().forEach {
                database.push(it)
            }
        }

    }

}