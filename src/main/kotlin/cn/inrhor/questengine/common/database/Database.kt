package cn.inrhor.questengine.common.database

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.database.type.DatabaseLocal
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.database.type.DatabaseSQL
import cn.inrhor.questengine.common.database.type.DatabaseType
import cn.inrhor.questengine.common.dialog.DialogManager.quitDialog
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.*
import taboolib.common.platform.function.*

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 创建任务数据
     */
    abstract fun createQuest(player: Player, questData: QuestData)

    /**
     * 删除任务数据
     */
    abstract fun removeQuest(player: Player, questID: String)

    /**
     * 创建目标条目数据
     */
    abstract fun createTarget(player: Player, targetData: TargetData)

    /**
     * 更新任务数据
     *
     * @param player 玩家
     * @param questID 任务编号
     * @param key 数据键
     * @param value 数据值
     */
    abstract fun updateQuest(player: Player, questID: String, key: String, value: Any)

    /**
     * 更新目标条目数据
     *
     * @param player 玩家
     * @param target 目标条目数据
     * @param key 数据键
     * @param value 数据值
     */
    abstract fun updateTarget(player: Player, target: TargetData, key: String, value: Any)

    /**
     * 添加标签
     */
    abstract fun addTag(player: Player, tag: String)

    /**
     * 移除标签
     */
    abstract fun removeTag(player: Player, tag: String)

    /**
     * 添加键值对数据
     */
    abstract fun addStorage(player: Player, key: String, value: Any)

    /**
     * 移除键值对数据
     */
    abstract fun removeStorage(player: Player, key: String)

    companion object {

        lateinit var database: Database

        fun initDatabase() {
            database = when (DatabaseManager.type) {
                DatabaseType.MYSQL -> DatabaseSQL()
                else -> DatabaseLocal()
            }
        }

        @SubscribeEvent
        fun join(ev: PlayerJoinEvent) {
            playerPull(ev.player)
        }

        fun playerPull(player: Player) {
            val uuid = player.uniqueId
            val pData = PlayerData(uuid)
            DataStorage.addPlayerData(uuid, pData)
            database.pull(player)
        }

    }

}