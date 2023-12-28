package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.manager.DataManager.storage
import cn.inrhor.questengine.api.manager.DataManager.tagsData
import cn.inrhor.questengine.api.manager.StorageManager.setStorage
import cn.inrhor.questengine.api.manager.TagsManager.addTag
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.utlis.time.toDate
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource


class DatabaseSQL: Database() {

    private val host = HostSQL(QuestEngine.config.getConfigurationSection("data.mysql")!!)

    private val table = QuestEngine.config.getString("data.mysql.table")

    private val tableUser = Table(table + "_user", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
    }

    private val tableQuest = Table(table + "_quest", host) {
        add { id() }
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("quest") { // questID
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("state") {
            type(ColumnTypeSQL.INT, 16)
        }
        add("time") {
            type(ColumnTypeSQL.DATETIME)
        }
        add("end") {
            type(ColumnTypeSQL.DATETIME) {
                def()
            }
        }
    }

    private val tableTarget = Table(table + "_target", host) {
        add("quest") { // mysql quest id
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("id") {// target id
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("schedule") {
            type(ColumnTypeSQL.INT)
        }
        add("state") {
            type(ColumnTypeSQL.INT, 16)
        }
    }

    private val tableTags = Table(table + "_tags", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("tag") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    private val tableStorage = Table(table + "_storage", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("key") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
        add("value") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    private val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableUser.workspace(source) { createTable() }.run()
        tableQuest.workspace(source) { createTable() }.run()
        tableTarget.workspace(source) { createTable() }.run()
        tableTags.workspace(source) { createTable() }.run()
        tableStorage.workspace(source) { createTable() }.run()
    }

    private fun userId(player: Player): Long {
        if (saveUserId.contains(player.uniqueId)) return saveUserId[player.uniqueId]!!
        val uId = tableUser.select(source) {
            rows("id")
            where { "uuid" eq player.uniqueId.toString() }
        }.map {
            getLong("id")
        }.firstOrNull() ?: -1L
        saveUserId[player.uniqueId] = uId
        return uId
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = uuid.getPlayerData()
        if (!tableUser.find(source) { where { "uuid" eq uuid.toString() } }) {
            tableUser.insert(source, "uuid") {
                value(uuid.toString())
            }
        }
        val uId = userId(player)
        tableQuest.select(source) {
            rows("id", "quest", "state", "time", "end")
            where { "user" eq uId }
        }.map {
            val questData = QuestData(
                id = getString("quest"),
                state = StateType.fromInt(getInt("state")),
                time = getDate("time").toStr(),
                end = getDate("end").toStr()
            )
            val questId = questData.id
            tableTarget.select(source) {
                rows("id", "schedule", "state")
                where { "quest" eq questId }
            }.map {
                val targetData = TargetData(
                    id = getString("id"),
                    questID = questId,
                    schedule = getInt("schedule"),
                    state = StateType.fromInt(getInt("state"))
                )
                questData.target.add(targetData)
            }
            questData.updateTime(player)
            pData.dataContainer.quest[questId] = questData
        }
        tableTags.select(source) {
            where { "user" eq uId }
            rows("tag")
        }.map {
            player.tagsData().tags.add(getString("tag"))
        }
        tableStorage.select(source) {
            where { "user" eq uId }
            rows("key", "value")
        }.map {
            player.storage()[getString("key")] = getString("value")
        }
    }

    override fun createQuest(player: Player, questData: QuestData) {
        val questID = questData.id
        val state = questData.state.int
        val uId = userId(player)
        tableQuest.insert(source, "user", "quest", "state", "time") {
            value(uId, questID, state, questData.time.toDate())
        }
    }

    override fun removeQuest(player: Player, questID: String) {
        val uId = userId(player)
        tableTarget.delete(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
        }
        tableQuest.delete(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
        }
    }

    override fun createTarget(player: Player, targetData: TargetData) {
        val uId = userId(player)
        val qId = findQuest(uId, targetData.questID)
        tableTarget.insert(source, "user", "quest", "id", "schedule", "state") {
            value(uId, qId, targetData.id, targetData.schedule, targetData.state.int)
        }
    }


    private fun findQuest(uId: Long, questID: String): Long {
        return tableQuest.select(source) {
            where { "user" eq uId and("quest" eq questID) }
        }.firstOrNull { getLong("id") } ?: -1L
    }

    override fun updateQuest(player: Player, questID: String, key: String, value: Any) {
        val uId = userId(player)
        tableQuest.update(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
            set(key, value)
        }
    }

    override fun updateTarget(player: Player, target: TargetData, key: String, value: Any) {
        val uId = userId(player)
        val qId = findQuest(uId, target.questID)
        tableTarget.update(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq qId
                    "id" eq target.id
                }
            }
            set(key, value)
        }
    }

    override fun addTag(player: Player, tag: String) {
        val uId = userId(player)
        tableTags.insert(source, "user", "tag") {
            value(uId, tag)
        }
    }

    override fun removeTag(player: Player, tag: String) {
        val uId = userId(player)
        tableTags.delete(source) {
            where {
                and {
                    "user" eq uId
                    "tag" eq tag
                }
            }
        }
    }

    override fun clearTag(player: Player) {
        tableTags.delete(source) {
            where {
                "user" eq player.uniqueId.toString()
            }
        }
    }

    override fun setStorage(player: Player, key: String, value: Any) {
        val uId = userId(player)
        tableStorage.insert(source, "user", "key", "value") {
            value(uId, key, value)
        }
    }

    override fun removeStorage(player: Player, key: String) {
        val uId = userId(player)
        tableStorage.delete(source) {
            where {
                and {
                    "user" eq uId
                    "key" eq key
                }
            }
        }
    }

    companion object {
        private val saveUserId = ConcurrentHashMap<UUID, Long>()
    }

}