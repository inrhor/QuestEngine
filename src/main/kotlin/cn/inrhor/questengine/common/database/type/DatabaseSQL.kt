package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.toStr
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.toState
import cn.inrhor.questengine.common.quest.toStr
import cn.inrhor.questengine.utlis.time.TimeUtil
import com.google.gson.Gson
import org.bukkit.entity.Player
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table
import java.util.*
import javax.sql.DataSource
import java.text.SimpleDateFormat


class DatabaseSQL: Database() {

    val host = HostSQL(QuestEngine.config.getConfigurationSection("data.mysql"))

    val table = QuestEngine.config.getString("data.mysql.table")

    val tableQuest = Table(table+"_user_quest", host) {
        add("uuid") {
            type(ColumnTypeSQL.TEXT)
        }
        add("questUUID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("questID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("innerQuestID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("state") {
            type(ColumnTypeSQL.TEXT)
        }
        add("finishedQuest") {
            type(ColumnTypeSQL.TEXT)
        }

    }

    val tableInnerQuest = Table(table+"_user_inner_quest", host) {
        add("uuid") {
            type(ColumnTypeSQL.TEXT)
        }
        add("questUUID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("innerQuestID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("state") {
            type(ColumnTypeSQL.TEXT)
        }
        add("rewards") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    val tableTargets = Table(table+"_user_targets", host) {
        add("uuid") {
            type(ColumnTypeSQL.TEXT)
        }
        add("questUUID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("name") {
            type(ColumnTypeSQL.TEXT)
        }
        add("innerQuestID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("schedule") {
            type(ColumnTypeSQL.INT)
        }
        add("timeDate") {
            type(ColumnTypeSQL.TEXT)
        }
        add("endDate") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    val tableControl = Table(table+"_user_control", host) {
        add("uuid") {
            type(ColumnTypeSQL.TEXT)
        }
        add("controlID") {
            type(ColumnTypeSQL.TEXT)
        }
        add("priority") {
            type(ColumnTypeSQL.TEXT)
        }
        add("line") {
            type(ColumnTypeSQL.INT)
        }
        add("waitTime") {
            type(ColumnTypeSQL.INT)
        }
    }

    val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableQuest.workspace(source) { createTable() }.run()
        tableTargets.workspace(source) { createTable() }.run()
        tableInnerQuest.workspace(source) { createTable() }.run()
        tableControl.workspace(source) { createTable() }.run()
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val uuidStr = uuid.toString()
        tableQuest.workspace(source) {
            select {
                where { "uuid" eq uuidStr }
                rows("questUUID", "questID", "innerQuestID", "state", "finishedQuest")
            }
        }.map {
            getString("questUUID") to getString("questID") to getString("innerQuestID") to getString("state") to getString("finishedQuest")
        }.forEach {
            val questUUID = UUID.fromString(it.first.first.first.first)
            val questID = it.first.first.first.second
            val innerID = it.first.first.second
            val innerData = getInnerQuestData(player, questUUID, questID, innerID)
            if (innerData != null) {
                val stateStr = it.first.second
                val state = stateStr.toState()
                val fmqJson = it.second
                val fmq = Gson().fromJson(fmqJson, MutableList::class.java) as MutableList<String>
                val questData = QuestData(questUUID, questID, innerData, state, null, fmq)
                pData.questDataList[questUUID] = questData
            }
        }
        tableControl.workspace(source) {
            select {
                where { "uuid" eq uuidStr }
                rows("controlID", "priority", "line", "waitTime")
            }
        }.map {
            getString("controlID") to getString("priority") to getInt("line") to getInt("waitTime")
        }.forEach {
            val controlID = it.first.first.first
            val priority = it.first.first.second
            val line = it.first.second
            val waitTime = it.second
            ControlManager.pullControl(player, controlID, priority, line, waitTime)
        }
    }

    override fun getInnerQuestData(player: Player, questUUID: UUID, questID: String, innerQuestID: String): QuestInnerData? {
        val uuid = player.uniqueId
        tableInnerQuest.workspace(source) {
            select {
                and { "uuid" eq uuid.toString()
                    and { "questUUID" eq questUUID.toString()
                        and { "innerQuestID" eq innerQuestID }
                    }
                }
                rows("state", "rewards")
            }
        }.map {
            getString("state") to getString("rewards")
        }.forEach {
            val innerModule = QuestManager.getInnerQuestModule(questID, innerQuestID)?: return@forEach
            val targets = returnTargets(player, questUUID, innerQuestID,
                QuestManager.getInnerModuleTargetMap(innerModule)
            )
            val stateStr = it.first
            val state = stateStr.toState()
            val rewardsStr = it.second
            val rewards = Gson().fromJson(rewardsStr, MutableMap::class.java) as MutableMap<String, Boolean>
            return QuestInnerData(questID, innerQuestID, targets, state, rewards)
        }
        return null
    }

    private fun returnTargets(player: Player, questUUID: UUID, innerQuestID: String,
                              targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        val uuid = player.uniqueId
        tableTargets.workspace(source) {
            select {
                and { "uuid" eq uuid.toString()
                    and { "questUUID" eq questUUID.toString()
                        and { "innerQuestID" eq innerQuestID }
                    }
                }
                rows("name", "schedule", "timeDate", "endDate")
            }
        }.map {
            getString("name") to getInt("schedule") to getString("timeDate") to getString("endDate")
        }.forEach {
            val name = it.first.first.first
            val targetData = targetDataMap[name]?: return@forEach
            targetData.schedule = it.first.first.second
            val timeDate = TimeUtil.strToDate(it.first.second)
            targetData.timeDate = timeDate
            val endTimeDate = TimeUtil.strToDate(it.second)
            targetData.endTimeDate = endTimeDate
            targetDataMap[name] = targetData
            targetData.runTime(player, questUUID)
        }
        return targetDataMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.forEach { (questUUID, questData) ->
            val questID = questData.questID
            val innerData = questData.questInnerData
            val innerQuestID = innerData.innerQuestID
            val state = questData.state.toStr()
            val fmq = questData.finishedList
            val fmqJson = Gson().toJson(fmq)
            tableQuest.workspace(source) {
                update {
                    and { "uuid" eq uuid.toString()
                        and { "questUUID" eq questUUID.toString()
                            and { "questID" eq questID
                                and { "innerQuestID" eq innerQuestID }
                            }
                        }
                    }
                    set("state", state)
                    set("finishedQuest", fmqJson)
                }
            }.run()
            updateInner(uuid, questUUID, innerData, innerQuestID)
        }
        pData.controlData.highestControls.forEach { (cID, cData) ->
            pushControl(uuid, cID, cData)
        }
    }

    private fun pushControl(uuid: UUID, controlID: String, cData: QuestControlData) {
        if (!ControlManager.needPush(controlID, cData.controlPriority)) return
        tableControl.workspace(source) {
            update {
                and { "uuid" eq uuid.toString()
                    and { "controlID" eq  controlID }
                }
                set("waitTime", cData.waitTime)
                set("line", cData.line)
            }
        }.run()
    }

    private fun updateInner(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData, innerQuestID: String) {
        val state = questInnerData.state.toStr()
        val rewards = Gson().toJson(questInnerData.rewardState)
        tableInnerQuest.workspace(source) {
            update {
                and { "uuid" eq uuid.toString()
                    and { "questUUID" eq questUUID.toString()
                        and { "innerQuestID" eq innerQuestID }
                    }
                }
                set("state", state)
                set("rewards", rewards)
            }
        }.run()
        updateTarget(uuid, questUUID, questInnerData)
    }

    fun createQuest(player: Player, questUUID: UUID, questData: QuestData) {
        val uuid = player.uniqueId
        val questID = questData.questID
        val innerData = questData.questInnerData
        val innerQuestID = innerData.innerQuestID
        val state = questData.state.toStr()
        val fmq = questData.finishedList
        val fmqJson = Gson().toJson(fmq)
        tableQuest.workspace(source) {
            insert("uuid", "questUUID", "questID", "innerQuestID", "state", "finishedQuest") {
                value(uuid.toString(), questUUID.toString(), questID, innerQuestID, state, fmqJson)
            }
        }.run()
        createInner(uuid, questUUID, innerData, innerQuestID)
    }

    private fun createInner(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData, innerID: String) {
        val state = questInnerData.state.toStr()
        val rewards = Gson().toJson(questInnerData.rewardState)
        tableInnerQuest.workspace(source) {
            insert("uuid", "questUUID", "innerQuestID", "state", "rewards") {
                value(uuid.toString(), questUUID.toString(), innerID, state, rewards)
            }
        }.run()
        createTarget(uuid, questUUID, questInnerData)
    }

    private fun createTarget(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData) {
        questInnerData.targetsData.forEach { (name, targetData) ->
            val innerID = questInnerData.innerQuestID
            val schedule = targetData.schedule
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateStr = dateFormat.format(targetData.timeDate)
            val endDateStr = dateFormat.format(targetData.endTimeDate)
            tableTargets.workspace(source) {
                insert("uuid", "questUUID", "name", "innerQuestID", "schedule", "timeDate", "endDate") {
                    value(uuid.toString(), questUUID.toString(), name, innerID, schedule, dateStr, endDateStr)
                }
            }.run()
        }
    }

    fun createControl(uuid: UUID, controlID: String, controlData: QuestControlData) {
        tableControl.workspace(source) {
            insert("uuid", "controlID", "priority", "line", "waitTime") {
                value(uuid.toString(), controlID, controlData.controlPriority.toStr(), 0, 0)
            }
        }.run()
    }

    private fun updateTarget(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData) {
        questInnerData.targetsData.forEach { (name, targetData) ->
            val innerID = questInnerData.innerQuestID
            val schedule = targetData.schedule
            tableTargets.workspace(source) {
                update {
                    and { "uuid" eq uuid.toString()
                        and { "questUUID" eq questUUID.toString()
                            and { "name" eq name
                                and { "innerQuestID" eq innerID }
                            }
                        }
                    }
                    set("schedule", schedule)
                }
            }.run()
        }
    }

    override fun removeQuest(player: Player, questData: QuestData) {
        val uuid = player.uniqueId.toString()
        val questUUID = questData.questUUID
        tableQuest.workspace(source) {
            delete {
                and { "uuid" eq uuid
                    and { "questUUID" eq questUUID.toString() }
                }
            }
        }.run()
        delete(uuid, questUUID)
    }


    override fun removeInnerQuest(player: Player, questUUID: UUID, questInnerData: QuestInnerData) {
        val uuid = player.uniqueId.toString()
        delete(uuid, questUUID)
    }

    private fun delete(uuid: String, questUUID: UUID) {
        tableInnerQuest.workspace(source) {
            delete {
                and { "uuid" eq uuid
                    and { "questUUID" eq questUUID.toString() }
                }
            }
        }.run()
        tableTargets.workspace(source) {
            delete {
                and { "uuid" eq uuid
                    and { "questUUID" eq questUUID.toString() }
                }
            }
        }.run()
    }

}