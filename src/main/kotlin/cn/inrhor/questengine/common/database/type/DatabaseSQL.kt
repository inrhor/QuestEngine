package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.manager.DataManager.setStorage
import cn.inrhor.questengine.api.manager.DataManager.storage
import cn.inrhor.questengine.api.manager.DataManager.tagsData
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

    val host = HostSQL(QuestEngine.config.getConfigurationSection("data.mysql")!!)

    val table = QuestEngine.config.getString("data.mysql.table")

    val tableUser = Table(table + "_user", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
    }

    val tableQuest = Table(table + "_quest", host) {
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

    val tableTarget = Table(table + "_target", host) {
        add("quest") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("id") {
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

    val tableTags = Table(table + "_tags", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("tag") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    val tableStorage = Table(table + "_storage", host) {
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

    val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableUser.workspace(source) { createTable() }.run()
        tableQuest.workspace(source) { createTable() }.run()
        tableTarget.workspace(source) { createTable() }.run()
        tableTags.workspace(source) { createTable() }.run()
        tableStorage.workspace(source) { createTable() }.run()
    }

    fun userId(player: Player): Long {
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
            getLong("id") to
                    getString("quest") to
                    getInt("state") to
                    getDate("time") to
                    getDate("end")
        }.forEach {
            val qId = it.first.first.first.first
            val questID = it.first.first.first.second
            val state = it.first.first.second
            val time = it.first.second
            val end = if (it.second == null) "" else it.second.toStr()
            val questData = QuestData(questID, mutableListOf(), StateType.fromInt(state), time.toStr(), end)
            questData.target = returnTargets(qId, questID)
            questData.updateTime(player)
            pData.dataContainer.quest[questID] = questData
        }
        tableTags.select(source) {
            where { "user" eq uId }
            rows("tag")
        }.map {
            getString("tag")
        }.forEach {
            player.tagsData().addTag(it)
        }
        tableStorage.select(source) {
            where { "user" eq uId }
            rows("key", "value")
        }.map {
            getString("key") to getString("value")
        }.forEach {
            player.setStorage(it.first, it.second)
        }
    }

    private fun returnTargets(qId: Long, questID: String): MutableList<TargetData> {
        val list = mutableListOf<TargetData>()
        tableTarget.select(source) {
            where { "quest" eq qId }
            rows("id", "schedule", "state")
        }.map {
            getString("id") to
                    getInt("schedule") to
                    getInt("state")
        }.forEach {
            val id = it.first.first
            val sc = it.first.second
            val st = it.second
            list.add(TargetData(id, questID, sc, StateType.fromInt(st)))
        }
        return list
    }

    override fun push(player: Player) {
        val pData = player.getPlayerData()
        val uId = userId(player)
        pData.dataContainer.quest.forEach { (questID, questData) ->
            val state = questData.state.int
            tableQuest.update(source) {
                where {
                    and {
                        "user" eq uId
                        "quest" eq questID
                    }
                }
                set("state", state)
                set("time", questData.time.toDate())
                if (questData.end.isNotEmpty()) {
                    set("end", questData.end.toDate())
                }
            }
            val qID = findQuest(uId, questID)
            updateTarget(qID, questData)
        }
        player.tagsData().tags.forEach {
            tableTags.update(source) {
                where {
                    "user" eq uId
                }
                set("tag", it)
            }
        }
        player.storage().forEach {
            tableStorage.update(source) {
                where {
                    "user" eq uId
                }
                set("key", it.key)
                set("value", it.value)
            }
        }
    }

    override fun createQuest(player: Player, questData: QuestData) {
        val questID = questData.id
        val state = questData.state.int
        val uId = userId(player)
        tableQuest.insert(source, "user", "quest", "state", "time") {
            value(uId, questID, state, questData.time.toDate())
        }
        val qID = findQuest(uId, questID)
        createTarget(player, qID, questData)
    }

    private fun createTarget(player: Player, qID: Long, questData: QuestData) {
        removeQuest(player, questData.id)
        questData.target.forEach {
            tableTarget.insert(source, "inner", "id", "name", "schedule") {
                value(qID, it.id, it.schedule, it.state.int)
            }
        }
    }

    private fun updateTarget(qId: Long, questData: QuestData) {
        questData.target.forEach {
            tableTarget.update(source) {
                where {
                    "quest" eq qId
                }
                set("schedule", it.schedule)
                set("state", it.state)
            }
        }
    }

    override fun removeQuest(player: Player, questID: String) {
        val uId = userId(player)
        tableQuest.delete(source) {
            where {
                and {
                    "user" eq uId
                    "quest" eq questID
                }
            }
        }
    }


    fun findQuest(uId: Long, questID: String): Long {
        return tableQuest.select(source) {
            where { "user" eq uId and("quest" eq questID) }
        }.firstOrNull { getLong("id") } ?: -1L
    }

    companion object {
        private val saveUserId = ConcurrentHashMap<UUID, Long>()
    }

}