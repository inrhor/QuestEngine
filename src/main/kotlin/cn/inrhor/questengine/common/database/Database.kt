package cn.inrhor.questengine.common.database

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.TrackData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.database.type.*
import cn.inrhor.questengine.common.nav.NavData
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.*
import java.util.UUID

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 创建任务数据
     */
    abstract fun createQuest(uuid: UUID, questData: QuestData)

    /**
     * 删除任务数据
     */
    abstract fun removeQuest(uuid: UUID, questID: String)

    /**
     * 创建目标条目数据
     */
    abstract fun createTarget(uuid: UUID, targetData: TargetData)

    /**
     * 更新任务数据
     *
     * @param uuid 玩家UUID
     * @param questID 任务编号
     * @param key 数据键
     * @param value 数据值
     */
    abstract fun updateQuest(uuid: UUID, questID: String, key: String, value: Any)

    /**
     * 更新目标条目数据
     *
     * @param uuid 玩家UUID
     * @param target 目标条目数据
     * @param key 数据键
     * @param value 数据值
     */
    abstract fun updateTarget(uuid: UUID, target: TargetData, key: String, value: Any)

    /**
     * 添加标签
     */
    abstract fun addTag(uuid: UUID, tag: String)

    /**
     * 移除标签
     */
    abstract fun removeTag(uuid: UUID, tag: String)

    /**
     * 清除标签
     */
    abstract fun clearTag(uuid: UUID)

    /**
     * 设置键值对数据
     */
    abstract fun setStorage(uuid: UUID, key: String, value: Any)

    /**
     * 移除键值对数据
     */
    abstract fun removeStorage(uuid: UUID, key: String)

    /**
     * 创建导航数据
     */
    abstract fun createNavigation(uuid: UUID, navId: String, navData: NavData)

    /**
     * 设置导航数据
     */
    abstract fun setNavigation(uuid: UUID, navId: String, key: String, value: Any)

    /**
     * 删除导航数据
     */
    abstract fun removeNavigation(uuid: UUID, navId: String)

    /**
     * 设置跟踪任务
     */
    abstract fun setTrack(uuid: UUID, trackData: TrackData)

    /**
     * 删除追踪任务
     */
    abstract fun removeTrack(uuid: UUID)

    companion object {

        lateinit var database: Database

        fun initDatabase() {
            database = when (DatabaseManager.type) {
                DatabaseType.LOCAL -> DatabaseSQLite()
                DatabaseType.MYSQL -> DatabaseSQL()
                else -> DatabaseError(IllegalStateException())
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