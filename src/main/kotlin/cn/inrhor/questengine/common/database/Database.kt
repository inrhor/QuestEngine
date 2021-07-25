package cn.inrhor.questengine.common.database

import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import org.bukkit.entity.Player
import java.util.*

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 为玩家上载数据
     */
    abstract fun push(player: Player)

    /**
     * 清除内部任务数据
     */
    abstract fun removeInnerQuest(player: Player, questUUID: UUID, questInnerData: QuestInnerData)

    /*@TListener
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

    }*/

}