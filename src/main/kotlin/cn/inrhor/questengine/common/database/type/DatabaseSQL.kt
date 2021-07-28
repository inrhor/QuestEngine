package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.QuestStateUtil
import io.izzel.taboolib.internal.gson.Gson
import io.izzel.taboolib.module.db.sql.*
import io.izzel.taboolib.module.db.sql.query.Where
import org.bukkit.entity.Player
import java.util.*
import javax.sql.DataSource
import java.text.SimpleDateFormat


class DatabaseSQL: Database() {

    val host = SQLHost(QuestEngine.config.getConfigurationSection("data.mysql"), QuestEngine.plugin, true)

    val table = QuestEngine.config.getString("data.mysql.table")

    val tableQuest = SQLTable(
        table+"_user_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questUUID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "innerQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256, "finishedQuest").columnOptions(SQLColumnOption.KEY)
    )

    val tableInnerQuest = SQLTable(
        table+"_user_inner_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questUUID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "innerQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36,"state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256,"rewards").columnOptions(SQLColumnOption.KEY)
    )

    val tableTargets = SQLTable(
        table+"_user_targets",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questUUID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(64, "name").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "innerQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.INT.toColumn("schedule").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "timeDate").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "endDate").columnOptions(SQLColumnOption.KEY)
    )

    val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableQuest.create(source)
        tableTargets.create(source)
        tableInnerQuest.create(source)
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        tableQuest.select(
            Where.equals("uuid", uuid.toString()))
            .row("questUUID")
            .row("questID")
            .row("innerQuestID")
            .row("state")
            .row("finishedQuest")
            .to(source)
            .map {
                it.getString("questUUID") to it.getString("questID") to it.getString("innerQuestID") to it.getString("state") to it.getString("finishedQuest")
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
        tableInnerQuest.select(
            Where.equals("uuid", uuid.toString()),
            Where.equals("questUUID", questUUID.toString()),
            Where.equals("questID", questID),
            Where.equals("innerQuestID", innerQuestID))
            .row("state")
            .row("rewards")
            .to(source)
            .map {
                it.getString("state") to it.getString("rewards")
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
        tableTargets.select(
            Where.equals("uuid", uuid.toString()),
            Where.equals("questUUID", questUUID.toString()),
            Where.equals("innerQuestID", innerQuestID))
            .row("name")
            .row("schedule")
            .row("timeDate")
            .row("endDate")
            .to(source)
            .map {
                it.getString("name") to it.getInt("schedule") to it.getString("timeDate") to it.getString("endDate")
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
            tableQuest.update(
                Where.equals("uuid", uuid.toString()),
                Where.equals("questUUID", questUUID.toString()),
                Where.equals("questID", questID),
                Where.equals("innerQuestID", innerQuestID))
                .set("state", state)
                .set("finishedQuest", fmqJson)
                .run(source)
            updateInner(uuid, questUUID, innerData, questID, innerQuestID)
        }
    }

    private fun updateInner(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData, questID: String, innerQuestID: String) {
        val state = QuestStateUtil.stateToStr(questInnerData.state)
        val rewards = Gson().toJson(questInnerData.rewardState)
        tableInnerQuest.update(
            Where.equals("uuid", uuid.toString()),
            Where.equals("questUUID", questUUID.toString()),
            Where.equals("questID", questID),
            Where.equals("innerQuestID", innerQuestID))
            .set("state", state)
            .set("rewards", rewards)
            .run(source)
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
        tableQuest.insert(uuid.toString(), questUUID.toString(), questID, innerQuestID, state, fmqJson).run(source)
        createInner(uuid, questUUID, innerData, questID, innerQuestID)
    }

    private fun createInner(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData, questID: String, innerID: String) {
        val state = QuestStateUtil.stateToStr(questInnerData.state)
        val rewards = Gson().toJson(questInnerData.rewardState)
        tableInnerQuest.insert(uuid.toString(), questUUID.toString(), questID, innerID, state, rewards).run(source)
        createTarget(uuid, questUUID, questInnerData)
    }

    private fun createTarget(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData) {
        questInnerData.targetsData.forEach { (name, targetData) ->
            val innerID = questInnerData.innerQuestID
            val schedule = targetData.schedule
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateStr = dateFormat.format(targetData.timeDate)
            val endDateStr = dateFormat.format(targetData.endTimeDate)
            tableTargets.insert(uuid.toString(), questUUID.toString(), name, innerID, schedule, dateStr, endDateStr).run(source)
        }
    }

    private fun updateTarget(uuid: UUID, questUUID: UUID, questInnerData: QuestInnerData) {
        questInnerData.targetsData.forEach { (name, targetData) ->
            val innerID = questInnerData.innerQuestID
            val schedule = targetData.schedule
            tableTargets.update(
                Where.equals("uuid", uuid.toString()),
                Where.equals("questUUID", questUUID.toString()),
                Where.equals("name", name),
                Where.equals("innerQuestID", innerID))
                .set("schedule", schedule)
                .run(source)
        }
    }

    override fun removeQuest(player: Player, questData: QuestData) {
        val uuid = player.uniqueId.toString()
        val questUUID = questData.questUUID
        tableQuest.delete(
            Where.equals("uuid", uuid),
            Where.equals("questUUID", questUUID.toString()))
            .run(source)
        tableInnerQuest.delete(
            Where.equals("uuid", uuid),
            Where.equals("questUUID", questUUID.toString()))
            .run(source)
        tableTargets.delete(
            Where.equals("uuid", uuid),
            Where.equals("questUUID", questUUID.toString()))
            .run(source)
    }


    override fun removeInnerQuest(player: Player, questUUID: UUID, questInnerData: QuestInnerData) {
        val uuid = player.uniqueId.toString()
        val questID = questInnerData.questID
        val innerID = questInnerData.innerQuestID
        tableInnerQuest.delete(
            Where.equals("uuid", uuid),
            Where.equals("questUUID", questUUID.toString()),
            Where.equals("questID", questID),
            Where.equals("innerQuestID", innerID))
            .run(source)
        tableTargets.delete(
            Where.equals("uuid", uuid),
            Where.equals("questUUID", questUUID.toString()),
            Where.equals("innerQuestID", innerID))
            .run(source)
    }

}