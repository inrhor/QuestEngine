package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.control.toControlPriority
import cn.inrhor.questengine.api.quest.control.toInt
import cn.inrhor.questengine.api.quest.control.toStr
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.common.quest.toInt
import cn.inrhor.questengine.common.quest.toState
import cn.inrhor.questengine.common.quest.toStr
import cn.inrhor.questengine.utlis.time.toDate
import org.bukkit.entity.Player
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table
import java.util.*


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
        add("index") {
            type(ColumnTypeSQL.INT) {
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
        add("time") {
            type(ColumnTypeSQL.DATETIME)
        }
        add("end") {
            type(ColumnTypeSQL.DATETIME)
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

    val tableTags = Table(table+"tags", host) {
        add("user") {
            type(ColumnTypeSQL.INT, 16){
                options(ColumnOptionSQL.KEY)
            }
        }
        add("tag") {
            type(ColumnTypeSQL.VARCHAR, 64)
        }
    }

    val source = host.createDataSource()

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
        return tableUser.select(source) {
            rows("id")
            where { "uuid" to player.uniqueId.toString() }
            limit(1)
        }.first { getLong("id") }
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val uId = userId(player)
        val q = tableQuest.name
        val n = tableInner.name
        tableQuest.select(source) {
            rows("uuid", "q_id", "state", "$n.n_id", "$n.state")
            where { "user" eq uId }
            innerJoin(n) {
                where { "$q.id" eq pre("$n.quest") }
            }
        }.map {
            getString("$q.uuid") to
                    getString("$q.q_id") to
                    getInt("$q.state") to
                    getString("$n.n_id") to
                    getInt("$n.state")
        }.forEach {
            val questUUID = UUID.fromString(it.first.first.first.first)
            val questID = it.first.first.first.second
            val qState = it.first.first.second
            val innerID = it.first.second
            val nState = it.second
            val qModule = QuestManager.getQuestModule(questID)?: return
            val nModule = QuestManager.getInnerQuestModule(questID, innerID)?: return
            val innerData = QuestInnerData(questID, innerID,
                QuestManager.getInnerModuleTargetMap(questUUID, qModule.mode.modeType(), nModule),
                nState.toState())
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
            ControlManager.pullControl(player, controlID, priority.toControlPriority().toStr(), line, wait)
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
                    "user" eq id
                    "id" eq tableFinish.name+".quest"
                    "quest" eq tableInner.name+".quest"
                }
            }
        }.map {
            getString(tableInner.name+".n_id")
        }.forEach {
            list.add(it)
        }
        return list
    }

    fun rewardMap(id: Long): MutableMap<String, Boolean> {
        val map = mutableMapOf<String, Boolean>()
        tableQuest.select(source) {
            rows(tableInner.name+".n_id")
            where {
                and {
                    "user" eq id
                    "id" eq tableInner.name+".quest"
                    tableInner.name+".n_id" eq tableReward.name+".inner"
                }
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
                    "user" eq uId
                    "uuid" eq questUUID.toString()
                    "id" eq tableInner.name+".quest"
                    "n_id" eq innerQuestID
                }
            }
            rows(tableQuest.name+".q_id", tableInner.name+".state")
        }.map {
            getString(tableQuest.name+".q_id") to getInt(tableInner.name+".state")
        }.forEach {
            val questID = it.first
            val questModule = QuestManager.getQuestModule(questID)?: return@forEach
            val innerModule = QuestManager.getInnerQuestModule(questID, innerQuestID)?: return@forEach
            val targets = returnTargets(player, questUUID, innerQuestID,
                QuestManager.getInnerModuleTargetMap(questUUID, questModule.mode.modeType(), innerModule)
            )
            val state = it.second.toState()
            return QuestInnerData(questID, innerQuestID, targets, state, rewardMap(uId))
        }
        return null
    }

    private fun returnTargets(player: Player, questUUID: UUID, innerQuestID: String,
                              targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        val uId = userId(player)
        val qId = findQuest(uId, questUUID)
        tableTarget.select(source) {
            rows("name", "schedule", "time", "end")
            where {
                and {
                    tableInner.name+".quest" eq qId
                    tableInner.name+".n_id" eq innerQuestID
                    "inner" eq tableInner.name+".inner"
                }
            }
        }.map {
            getString("name") to getInt("schedule") to getDate("time") to getDate("end")
        }.forEach {
            val name = it.first.first.first
            val targetData = targetDataMap[name]?: return@forEach
            targetData.schedule = it.first.first.second
            val timeDate = it.first.second
            targetData.timeDate = timeDate
            val endTimeDate = it.second?: null
            targetData.endTimeDate = endTimeDate
            targetDataMap[name] = targetData
            targetData.runTime(player, questUUID)
        }
        return targetDataMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val uId = userId(player)
        pData.questDataList.forEach { (questUUID, questData) ->
            val innerData = questData.questInnerData
            val state = questData.state.toStr()
            val fmq = questData.finishedList
            tableQuest.update(source) {
                where {
                    and {
                        uuid.toString() eq tableInner.name+".uuid"
                        "user" eq tableUser.name+".id"
                        "uuid" eq questUUID.toString()
                    }
                }
                set("inner", innerData)
                set("state", state)
            }
            updateInner(uId, questUUID, innerData)
            fmq.forEach {
                tableInner.select(source) {
                    rows("id")
                    where {
                        and {
                            tableQuest.name+".user" eq uId
                            tableQuest.name+".uuid" eq questUUID
                            "quest" eq tableQuest.name+".id"
                            "n_id" eq it
                        }
                    }
                }.map {
                    getLong("id") to getLong(tableQuest.name+".id")
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
        tableInner.update(source) {
            where {
                and {
                    tableQuest.name+".user" eq id
                    tableQuest.name+".id" eq "quest"
                }
            }
            set("state", state)
        }
        updateTarget(id, questUUID, questInnerData)
    }

    override fun createQuest(player: Player, questUUID: UUID, questData: QuestData) {
        val questID = questData.questID
        val innerData = questData.questInnerData
        val innerQuestID = innerData.innerQuestID
        val state = questData.state.toInt()
        val fmq = questData.finishedList
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
        }.first { getLong("id") }
        createInner(uId, qId, innerData)
    }

    private fun createInner(uId: Long, qId: Long, questInnerData: QuestInnerData) {
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
        }.first { getLong("id") }
        createTarget(nId, questInnerData)
    }

    private fun createTarget(nId: Long, questInnerData: QuestInnerData) {
        var index = 0
        questInnerData.targetsData.forEach { (name, targetData) ->
            val schedule = targetData.schedule
            tableTarget.insert(source, "inner", "index", "name", "schedule", "time") {
                value(nId, index, name, schedule, targetData.timeDate)
            }
            tableTarget.update(source) {
                where {
                    and {
                        "inner" eq nId
                        "index" eq index
                        "name" eq name
                    }
                }
                set("end", targetData.endTimeDate)
            }
            index++
        }
    }

    private fun updateTarget(id: Long, questUUID: UUID, questInnerData: QuestInnerData) {
        var index = 0
        questInnerData.targetsData.values.forEach {
            val innerID = questInnerData.innerQuestID
            val schedule = it.schedule
            tableTarget.update(source) {
                where {
                    and {
                        tableQuest.name + ".user" eq id
                        tableQuest.name + ".uuid" eq questUUID.toString()
                        "quest" eq tableQuest.name + ".id"
                        "n_id" eq innerID
                        "index" eq index
                    }
                }
                set("schedule", schedule)
            }
            index++
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
        }.first { getLong("id") }
    }

    private fun delete(uId: Long, questUUID: UUID) {
        val qId = findQuest(uId, questUUID)
        tableReward.delete(source) {
            where { "quest" eq qId }
        }
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

}