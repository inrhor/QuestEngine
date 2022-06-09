package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.control.toInt
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.database.data.tagsData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.common.quest.enum.toInt
import cn.inrhor.questengine.common.quest.enum.toState
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

    val tableUser = Table(table+"_user", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
    }

    val tableQuest = Table(table+"_quest", host) {
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

    val tableTarget = Table(table+"_target", host) {
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

    val tableTags = Table(table+"_tags", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16){
                options(ColumnOptionSQL.KEY)
            }
        }
        add("tag") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableUser.workspace(source) {createTable()}.run()
        tableQuest.workspace(source) { createTable() }.run()
        tableTarget.workspace(source) { createTable() }.run()
        tableTags.workspace(source) { createTable() }.run()
    }

    fun userId(player: Player): Long {
        if (saveUserId.contains(player.uniqueId)) return saveUserId[player.uniqueId]!!
        val uId = tableUser.select(source) {
            rows("id")
            where { "uuid" eq player.uniqueId.toString() }
        }.map {
            getLong("id")
        }.firstOrNull()?: -1L
        saveUserId[player.uniqueId] = uId
        return uId
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = uuid.getPlayerData()
        if (!tableUser.find(source) {where { "uuid" eq uuid.toString() }}) {
            tableUser.insert(source, "uuid") {
                value(uuid.toString())
            }
        }
        val uId = userId(player)
        tableQuest.select(source) {
            rows("id", "quest", "state", "time", "end")
            where { "user" eq uId}
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
            questData.target = returnTargets(qId)
        }
        tableTags.select(source) {
            where { "user" eq uId }
            rows("tag")
        }.map {
            getString("tag")
        }.forEach {
            player.tagsData().addTag(it)
        }
    }

    private fun returnTargets(qId: Long): MutableList<TargetData> {
        val list = mutableListOf<TargetData>()
        tableTarget.select(source) {

        }
        return list
    }

/*        override fun push(player: Player) {
        val pData = DataStorage.getPlayerData(player.uniqueId)
        val uId = userId(player)
        pData.questDataList.forEach { (questUUID, questData) ->
            val innerData = questData.questInnerData
            val state = questData.state.toInt()
            val fmq = questData.finishedList
            val qId = findQuest(uId, questUUID)
            val nId = tableInner.select(source) {
                rows(tableInner.name + ".id")
                where { tableInner.name + ".quest" eq qId and (tableInner.name + ".n_id" eq innerData.innerQuestID) }
            }.firstOrNull { getLong(tableInner.name + ".id") } ?: return
            tableQuest.update(source) {
                where {
                    "id" eq qId
                }
                set("inner", nId)
                set("state", state)
            }
            updateInner(uId, questUUID, innerData)
            fmq.forEach {
                tableInner.select(source) {
                    rows("id")
                    where {
                        and {
                            "n_id" eq it
                            tableQuest.name + ".user" eq uId
                            tableQuest.name + ".uuid" eq questUUID
                        }
                    }
                    innerJoin(tableQuest.name) {
                        where { tableQuest.name + ".id" eq pre(tableInner.name + ".quest") }
                    }
                }.map {
                    getLong("id") to getLong(tableQuest.name + ".id")
                }.forEach {
                    updateFinish(it.second, it.first)
                }
            }
        }
        val cData = pData.controlQueue
        cData.highestControls.forEach { (cID, cData) ->
            pushControl(uId, cID, cData)
        }
        cData.controls.forEach { (cID, cData) ->
            pushControl(uId, cID, cData)
        }
        pData.tagsData.list().forEach {
            tableQuest.update(source) {
                where {
                    "user" eq uId
                }
                set("tag", it)
            }
        }
    }

    override fun createQuest(player: Player, questUUID: UUID, questData: GroupData) {
        val questID = questData.id
        val innerData = questData.questInnerData
        val state = questData.state.toInt()
        val uId = userId(player)
        tableQuest.insert(source, "user", "uuid", "q_id", "inner", "state") {
            value(uId, questUUID.toString(), questID, -1, state)
        }
        val qId = tableQuest.select(source) {
            rows("id")
            where {
                and {
                    "user" eq uId
                    "uuid" eq questUUID.toString()
                }
            }
            limit(1)
        }.firstOrNull { getLong("id") } ?: -1L
        createInner(qId, innerData)
    }

    private fun createTarget(nId: Long, questInnerData: QuestData) {
        questInnerData.target.forEach { (a, e) ->
            val schedule = e.schedule
            tableTarget.insert(source, "inner", "id", "name", "schedule") {
                value(nId, a, e.name, schedule)
            }
        }
    }

    private fun updateTarget(qId: Long, questInnerData: QuestData) {
        val nId = findInner(qId, questInnerData.id)
        questInnerData.target.forEach { (a, e) ->
            val schedule = e.schedule
            tableTarget.update(source) {
                where {
                    and {
                        "inner" eq nId
                        "id" eq a
                    }
                }
                set("schedule", schedule)
            }
        }
    }

    override fun removeQuest(player: Player, questData: GroupData) {
        val questUUID = questData.uuid
        val uId = userId(player)
        delete(uId, questUUID)
        tableQuest.delete(source) {
            where {
                and {
                    "user" eq uId
                    "uuid" eq questUUID.toString()
                }
            }
        }
    }

    fun findQuest(uId: Long, questUUID: UUID): Long {
        return tableQuest.select(source) {
            where { "user" eq uId and("uuid" eq questUUID.toString()) }
        }.firstOrNull { getLong("id") } ?: -1L
    }

    private fun delete(uId: Long, questUUID: UUID) {
        val qId = findQuest(uId, questUUID)
        tableInner.select(source) {
            rows("id")
            where { "quest" eq qId }
        }.map {
            getLong("id")
        }.forEach {
            tableTarget.delete(source) {
                where { "inner" eq it }
            }
        }
        tableInner.delete(source) {
            where {
                "quest" eq findQuest(uId, questUUID)
            }
        }
    }*/

    companion object {
        private val saveUserId = ConcurrentHashMap<UUID, Long>()
    }

}