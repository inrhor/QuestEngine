package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.control.toControlPriority
import cn.inrhor.questengine.api.quest.control.toInt
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.common.quest.toInt
import cn.inrhor.questengine.common.quest.toState
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
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("q_id") { // questID
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("inner") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("state") {
            type(ColumnTypeSQL.INT, 16)
        }
    }

    val tableInner = Table(table+"_inner", host) {
        add { id() }
        add("quest") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("n_id") { // innerID
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

    val tableFinish = Table(table+"_finish", host) {
        add("quest") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("inner") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
    }

    val tableReward = Table(table+"_reward", host) {
        add("inner") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("r_id") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("get") {
            type(ColumnTypeSQL.BOOL)
        }
    }

    val tableTarget = Table(table+"_target", host) {
        add("inner") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("id") {
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("name") {
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("schedule") {
            type(ColumnTypeSQL.INT)
        }
    }

    val tableControl = Table(table+"_control", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("c_id") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("priority") {
            type(ColumnTypeSQL.TEXT)
        }
        add("line") {
            type(ColumnTypeSQL.INT)
        }
        add("wait") {
            type(ColumnTypeSQL.INT)
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
        tableInner.workspace(source) { createTable() }.run()
        tableFinish.workspace(source) { createTable() }.run()
        tableReward.workspace(source) { createTable() }.run()
        tableControl.workspace(source) { createTable() }.run()
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
        val pData = DataStorage.getPlayerData(uuid)
        if (!tableUser.find(source) {where { "uuid" eq uuid.toString() }}) {
            tableUser.insert(source, "uuid") {
                value(uuid.toString())
            }
        }
        val uId = userId(player)
        tableQuest.select(source) {
            rows(tableQuest.name+".uuid", tableQuest.name+".q_id", tableQuest.name+".state", tableInner.name+".n_id", tableInner.name+".state")
            where { "user" eq uId}
            innerJoin(tableInner.name) {
                where { tableQuest.name+".id" eq pre(tableInner.name+".quest") }
            }
        }.map {
            getString(tableQuest.name+".uuid") to
                    getString(tableQuest.name+".q_id") to
                    getInt(tableQuest.name+".state") to
                    getString(tableInner.name+".n_id") to
                    getInt(tableInner.name+".state") to
                    getDate(tableInner.name+".time") to
                    getDate(tableInner.name+".end")
        }.forEach {
            val questUUID = UUID.fromString(it.first.first.first.first.first.first)
            val questID = it.first.first.first.first.first.second
            val qState = it.first.first.first.first.second
            val innerID = it.first.first.first.second
            val nState = it.first.first.second
            val time = it.first.second
            val end = it.second?: null
            val qModule = QuestManager.getQuestModule(questID)?: return
            val nModule = QuestManager.getInnerQuestModule(questID, innerID)?: return
            val innerData = QuestInnerData(questID, innerID,
                QuestManager.getInnerModuleTargetMap(questUUID, qModule.mode.type, nModule),
                nState.toState(), time, end, rewardMap(uId, questUUID))
            val questData = QuestData(questUUID, questID, innerData, qState.toState(), null, finishInner(uId))
            pData.questDataList[questUUID] = questData
            QuestManager.checkTimeTask(player, questUUID, questID)
        }
        tableControl.select(source) {
            where { "user" eq uId }
            rows("c_id", "priority", "line", "wait")
        }.map {
            getString("c_id") to getInt("priority") to getInt("line") to getInt("wait")
        }.forEach {
            val controlID = it.first.first.first
            val priority = it.first.first.second
            val line = it.first.second
            val wait = it.second
            ControlManager.pullControl(player, controlID, priority.toControlPriority().toString(), line, wait)
        }
        TargetManager.runTask(pData, player)
        tableTags.select(source) {
            where { "user" eq uId }
            rows("tag")
        }.map {
            getString("tag")
        }.forEach {
            pData.tagsData.addTag(it)
        }
    }

    fun finishInner(id: Long): MutableSet<String> {
        val list = mutableSetOf<String>()
        tableQuest.select(source) {
            rows(tableInner.name+".n_id")
            where {
                and {
                    tableQuest.name+".user" eq id
                    tableQuest.name+".id" eq tableFinish.name+".quest"
                }
            }
            innerJoin(tableInner.name) {
                tableQuest.name+".id" eq pre(tableInner.name+".quest")
            }
        }.map {
            getString(tableInner.name+".n_id")
        }.forEach {
            list.add(it)
        }
        return list
    }

    fun rewardMap(uId: Long, questUUID: UUID): MutableMap<String, Boolean> {
        val map = mutableMapOf<String, Boolean>()
        val qId = findQuest(uId, questUUID)
        tableInner.select(source) {
            rows("n_id")
            where { tableInner.name+".quest" eq qId }
            innerJoin(tableReward.name) {
                where { tableReward.name+".inner" eq tableInner.name+".inner" }
            }
        }.map {
            getString(tableReward.name+".r_id") to getBoolean(tableReward.name+".get")
        }.forEach {
            map[it.first] = it.second
        }
        return map
    }

    override fun getInnerQuestData(player: Player, questUUID: UUID, innerQuestID: String): QuestInnerData? {
        val uId = userId(player)
        tableQuest.select(source) {
            where {
                and {
                    tableQuest.name+".user" eq uId
                    tableQuest.name+".uuid" eq questUUID.toString()
                }
            }
            innerJoin(tableInner.name) {
                tableInner.name+".n_id" eq innerQuestID
                tableInner.name+".quest" eq pre(tableQuest.name+".id")
            }
            rows(tableQuest.name+".q_id", tableInner.name+".state")
        }.map {
            getString(tableQuest.name+".q_id") to getInt(tableInner.name+".state") to
                    getDate(tableInner.name+".time") to getDate(tableInner.name+".end")
        }.forEach {
            val questID = it.first.first.first
            val questModule = QuestManager.getQuestModule(questID)?: return@forEach
            val innerModule = QuestManager.getInnerQuestModule(questID, innerQuestID)?: return@forEach
            val targets = returnTargets(player, questUUID, innerQuestID,
                QuestManager.getInnerModuleTargetMap(questUUID, questModule.mode.type, innerModule)
            )
            val state = it.first.first.second.toState()
            val time = it.first.second
            val end = it.second?: null
            return QuestInnerData(questID, innerQuestID, targets, state, time, end, rewardMap(uId, questUUID))
        }
        return null
    }

    private fun returnTargets(player: Player, questUUID: UUID, innerQuestID: String,
                              targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        val uId = userId(player)
        val qId = findQuest(uId, questUUID)
        val nId = findInner(qId, innerQuestID)
        tableTarget.select(source) {
            rows("id", "schedule")
            where {
                "inner" eq nId
            }
        }.map {
            getString("id") to getInt("schedule")
        }.forEach {
            val id = it.first
            val targetData = targetDataMap[id]?: return@forEach
            targetData.schedule = it.second
        }
        return targetDataMap
    }

    override fun push(player: Player) {
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
        val cData = pData.controlData
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

    fun updateFinish(qId: Long, nId: Long) {
        if (!(tableFinish.find(source) {
                where { "quest" eq qId and("inner" eq nId) }
            }))  {
            tableFinish.insert(source, "quest", "inner") {
                value(qId, nId)
            }
        }
    }

    private fun hasControl(uId: Long, controlID: String): Boolean {
        return tableControl.find(source) {
            where {
                and {
                    "user" eq uId
                    "c_id" eq  controlID
                }
            }
        }
    }

    private fun pushControl(uId: Long, controlID: String, cData: QuestControlData) {
        if (ControlManager.runLogType(controlID) == RunLogType.DISABLE) return
        val line = cData.line
        val waitTime = cData.waitTime
        if (hasControl(uId, controlID)) {
            tableControl.update(source) {
                where {
                    and {
                        "user" eq uId
                        "c_id" eq  controlID
                    }
                }
                set("line", line)
                set("wait", waitTime)
            }
        }else {
            tableControl.insert(source, "user", "c_id", "priority", "line", "wait") {
                value(uId, controlID, cData.controlPriority.toInt(), line, waitTime)
            }
        }
    }

    private fun updateInner(id: Long, questUUID: UUID, questInnerData: QuestInnerData) {
        val state = questInnerData.state.toInt()
        val qId = findQuest(id, questUUID)
        tableInner.update(source) {
            where {
                and {
                    "quest" eq qId
                }
            }
            set("state", state)
            set("time", questInnerData.timeDate)
            set("end",questInnerData.end)
        }
        val nId = findInner(qId, questInnerData.innerQuestID)
        if (!tableReward.find(source) { where { "inner" eq nId } }) {
            questInnerData.rewardState.forEach { (t, u) ->
                tableReward.insert(source, "inner", "r_id", "get") {
                    value(nId, t, u)
                }
            }
        }else {
            questInnerData.rewardState.forEach { (t, u) ->
                tableReward.update(source) {
                    where { "inner" eq nId and("r_id" eq t)}
                    set("get", u)
                }
            }
        }
        updateTarget(qId, questInnerData)
    }

    override fun createQuest(player: Player, questUUID: UUID, questData: QuestData) {
        val questID = questData.questID
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

    private fun createInner(qId: Long, questInnerData: QuestInnerData) {
        val state = questInnerData.state.toInt()
        tableInner.insert(source, "quest", "n_id", "state") {
            value(qId, questInnerData.innerQuestID, state)
        }
        val nId = tableInner.select(source) {
            rows("id")
            where {
                and {
                    "quest" eq qId
                    "n_id" eq questInnerData.innerQuestID
                    "state" eq state
                }
            }
            limit(1)
        }.firstOrNull { getLong("id") } ?: -1L
        createTarget(nId, questInnerData)
    }

    private fun createTarget(nId: Long, questInnerData: QuestInnerData) {
        questInnerData.targetsData.forEach { (id, targetData) ->
            val schedule = targetData.schedule
            tableTarget.insert(source, "inner", "id", "name", "schedule") {
                value(nId, id, targetData.name, schedule)
            }
        }
    }

    fun findInner(qId: Long, innerID: String): Long {
        return tableInner.select(source) {
            where { "quest" eq qId and("n_id" eq innerID) }
        }.firstOrNull { getLong("id") }?: -1L
    }

    private fun updateTarget(qId: Long, questInnerData: QuestInnerData) {
        val nId = findInner(qId, questInnerData.innerQuestID)
        questInnerData.targetsData.forEach { (t, u) ->
            val schedule = u.schedule
            tableTarget.update(source) {
                where {
                    and {
                        "inner" eq nId
                        "id" eq t
                    }
                }
                set("schedule", schedule)
            }
        }
    }

    override fun removeQuest(player: Player, questData: QuestData) {
        val questUUID = questData.questUUID
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

    override fun removeControl(player: Player, controlID: String) {
        tableControl.delete(source) {
            where { "user" eq userId(player) }
        }
    }

    override fun removeInnerQuest(player: Player, questUUID: UUID) {
        delete(userId(player), questUUID)
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
            tableReward.delete(source) {
                where { "inner" eq it }
            }
        }
        tableInner.delete(source) {
            where {
                "quest" eq findQuest(uId, questUUID)
            }
        }
    }

    companion object {
        private val saveUserId = ConcurrentHashMap<UUID, Long>()
    }

}