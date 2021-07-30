package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.QuestStateUtil
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
        add("schedule") {
            type(ColumnTypeSQL.INT)
        }
        add("timeDate") {
            type(ColumnTypeSQL.DATE)
        }
        add("endDate") {
            type(ColumnTypeSQL.DATE)
        }
    }

    val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableQuest.workspace(source) { createTable() }
        tableTargets.workspace(source) { createTable() }
        tableInnerQuest.workspace(source) { createTable() }
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        tableQuest.workspace(source) {
            select {
                where { "uuid" eq uuid.toString() }
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
                val state = QuestStateUtil.strToState(stateStr)
                val fmqJson = it.second
                val fmq = Gson().fromJson(fmqJson, MutableList::class.java) as MutableList<String>
                val questData = QuestData(questUUID, questID, innerData, state, null, fmq)
                pData.questDataList[questUUID] = questData
            }
        }
    }

    override fun getInnerQuestData(player: Player, questUUID: UUID, questID: String, innerQuestID: String): QuestInnerData? {
        val uuid = player.uniqueId
        tableInnerQuest.workspace(source) {
            select {
                where { "uuid" eq uuid.toString()
                    where { "questUUID" eq questUUID.toString()
                        where { "innerQuestID" eq innerQuestID }
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
            val state = QuestStateUtil.strToState(stateStr)
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
                where { "uuid" eq uuid.toString()
                    where { "questUUID" eq questUUID.toString()
                        where { "innerQuestID" eq innerQuestID }
                    }
                }
                rows("state", "rewards")
            }
        }.map {
            getString("name") to getInt("schedule") to getString("timeDate") to getString("endDate")
        }.forEach {
            val name = it.first.first.first
            val targetData = targetDataMap[name]?: return@forEach
            targetData.schedule = it.first.first.second
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timeDate = dateFormat.parse(it.first.second)
            targetData.timeDate = timeDate
            val endTimeDate = dateFormat.parse(it.first.second)
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
            val state = QuestStateUtil.stateToStr(questData.state)
            val fmq = questData.finishedList
            val fmqJson = Gson().toJson(fmq)
            tableQuest.workspace(source) {
                update {
                    where { "uuid" eq uuid.toString()
                        where { "questUUID" eq questUUID.toString()
                            where { "questID" eq questID
                                where { "innerQuestID" eq innerQuestID }
                            }
                        }
                    }
                    set("state", state)
                    set("finishedQuest", fmqJson)
                }
            }
            updateInner(uuid, questUUID, innerData, questID, innerQuestID)
        }
    }

    private fun updateInner(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData, questID: String, innerQuestID: String) {
        val state = QuestStateUtil.stateToStr(questInnerData.state)
        val rewards = Gson().toJson(questInnerData.rewardState)
        tableInnerQuest.workspace(source) {
            update {
                where { "uuid" eq uuid.toString()
                    where { "questUUID" eq questUUID.toString()
                        where { "innerQuestID" eq innerQuestID }
                    }
                }
                set("state", state)
                set("finishedQuest", rewards)
            }
        }
        updateTarget(uuid, questUUID, questInnerData)
    }

    fun create(player: Player, questUUID: UUID, questData: QuestData) {
        val uuid = player.uniqueId
        val questID = questData.questID
        val innerData = questData.questInnerData
        val innerQuestID = innerData.innerQuestID
        val state = QuestStateUtil.stateToStr(questData.state)
        val fmq = questData.finishedList
        val fmqJson = Gson().toJson(fmq)
        tableQuest.workspace(source) {
            insert("uuid", "questUUID", "questID", "innerQuestID", "state", "finishedQuest") {
                value(uuid.toString(), questUUID.toString(), questID, innerQuestID, state, fmqJson)
            }
        }
        createInner(uuid, questUUID, innerData, questID, innerQuestID)
    }

    private fun createInner(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData, questID: String, innerID: String) {
        val state = QuestStateUtil.stateToStr(questInnerData.state)
        val rewards = Gson().toJson(questInnerData.rewardState)
        tableInnerQuest.workspace(source) {
            insert("uuid", "questUUID", "innerQuestID", "state", "rewards") {
                value(uuid.toString(), questUUID.toString(), innerID, state, rewards)
            }
        }
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
            }
        }
    }

    private fun updateTarget(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData) {
        questInnerData.targetsData.forEach { (name, targetData) ->
            val innerID = questInnerData.innerQuestID
            val schedule = targetData.schedule
            tableTargets.workspace(source) {
                update {
                    where { "uuid" eq uuid.toString()
                        where { "questUUID" eq questUUID.toString()
                            where { "name" eq name
                                where { "innerQuestID" eq innerID }
                            }
                        }
                    }
                    set("schedule", schedule)
                }
            }
        }
    }

    override fun removeQuest(player: Player, questData: QuestData) {
        val uuid = player.uniqueId.toString()
        val questUUID = questData.questUUID
        tableQuest.workspace(source) {
            delete {
                where { "uuid" eq uuid
                    where { "questUUID" eq questUUID.toString() }
                }
            }
        }
        delete(uuid, questUUID)
    }


    override fun removeInnerQuest(player: Player, questUUID: UUID, questInnerData: QuestInnerData) {
        val uuid = player.uniqueId.toString()
        delete(uuid, questUUID)
    }

    private fun delete(uuid: String, questUUID: UUID) {
        tableInnerQuest.workspace(source) {
            delete {
                where { "uuid" eq uuid
                    where { "questUUID" eq questUUID.toString() }
                }
            }
        }
        tableTargets.workspace(source) {
            delete {
                where { "uuid" eq uuid
                    where { "questUUID" eq questUUID.toString() }
                }
            }
        }
    }

}