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

class DatabaseSQL: Database() {

    val host = SQLHost(QuestEngine.config.getConfigurationSection("data.mysql"), QuestEngine.plugin, true)

    val table = QuestEngine.config.getString("data.mysql.table")

    val tableQuest = SQLTable(
        table+"_user_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questMainID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256, "finishedQuest").columnOptions(SQLColumnOption.KEY)
    )

    val tableMainQuest = SQLTable(
        table+"_user_main_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36,"state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256,"rewards").columnOptions(SQLColumnOption.KEY)
    )

    val tableTargets = SQLTable(
        table+"_user_targets",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(64, "name").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "subQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.INT.toColumn("time").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.INT.toColumn("schedule").columnOptions(SQLColumnOption.KEY)
    )

    val source: DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableQuest.create(source)
        tableTargets.create(source)
        tableMainQuest.create(source)
        tableSubQuest.create(source)
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        tableQuest.select(
            Where.equals("uuid", uuid.toString()))
            .row("questID")
            .row("questMainID")
            .row("state")
            .row("finishedQuest")
            .to(source)
            .map {
                it.getString("questID") to it.getString("questMainID") to it.getString("state") to it.getString("finishedMainQuest")
            }.forEach {
                val questID = it.first.first.first
                val mainID = it.first.first.second
                val mainData = pullMain(uuid, questID, mainID)
                if (mainData != null) {
                    val stateStr = it.first.second
                    val state = QuestStateUtil.strToState(stateStr)
                    val fmqJson = it.second
                    val fmq = Gson().fromJson(fmqJson, MutableList::class.java) as MutableList<String>
                    val questData = QuestData(questID, mainData, state, null, fmq)
                    pData.questDataList[questID] = questData
                }
            }
    }

    private fun pullMain(uuid: UUID, questID: String, mainQuestID: String): QuestMainData? {
        tableMainQuest.select(
            Where.equals("uuid", uuid.toString()),
            Where.equals("questID", questID),
            Where.equals("mainQuestID", mainQuestID))
            .row("state")
            .row("rewards")
            .to(source)
            .map {
                it.getString("state") to it.getString("targets") to it.getString("rewards")
            }.forEach {
                val mainModule = QuestManager.getInnerQuestModule(questID, mainQuestID)
                if (mainModule != null) {
                    val targets = returnTargets(
                        uuid, questID, mainQuestID, "",
                        QuestManager.getInnerModuleTargetMap(mainModule)
                    )
                    val stateStr = it.first.first
                    val state = QuestStateUtil.strToState(stateStr)
                    val rewardsStr = it.second
                    val rewards = Gson().fromJson(rewardsStr, MutableMap::class.java) as MutableMap<String, Boolean>
                    val subMap = pullSub(uuid, questID, mainQuestID)
                    return QuestMainData(questID, mainQuestID, subMap, targets, state, rewards)
                }
            }
        return null
    }

    private fun pullSub(uuid: UUID, questID: String, mainQuestID: String): MutableMap<String, QuestSubData> {
        val subMap = mutableMapOf<String, QuestSubData>()
        tableSubQuest.select(
            Where.equals("uuid", uuid.toString()),
            Where.equals("questID", questID),
            Where.equals("mainQuestID", mainQuestID))
            .row("subQuestID")
            .row("state")
            .row("rewards")
            .to(source)
            .map {
                it.getString("subQuestID") to it.getString("state") to it.getString("rewards")
            }.forEach {
                val mainModule = QuestManager.getInnerQuestModule(questID, mainQuestID)
                if (mainModule != null) {
                    val subQuestID = it.first.first
                    val targets = returnTargets(uuid, questID, mainQuestID, subQuestID,
                        QuestManager.getInnerModuleTargetMap(mainModule))
                    val stateStr = it.first.second
                    val state = QuestStateUtil.strToState(stateStr)
                    val rewardsStr = it.second
                    val rewards = Gson().fromJson(rewardsStr, MutableMap::class.java) as MutableMap<String, Boolean>
                    val subData = QuestSubData(questID, mainQuestID, subQuestID, targets, state, rewards)
                    subMap[subQuestID] = subData
                }
            }
        return subMap
    }

    private fun returnTargets(uuid: UUID,
                      questID: String, mainQuestID: String, subQuestID: String,
                      targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        tableTargets.select(
            Where.equals("uuid", uuid.toString()),
            Where.equals("questID", questID),
            Where.equals("mainQuestID", mainQuestID),
            Where.equals("subQuestID", subQuestID))
            .row("name")
            .row("time")
            .row("schedule")
            .to(source)
            .map {
                it.getString("name") to it.getInt("time") to it.getInt("schedule")
            }.forEach {
                val name = it.first.first
                val targetData = targetDataMap[name]?: return@forEach
                targetData.time = it.first.second
                targetData.schedule = it.second
            }
        return targetDataMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.forEach { (questID, questData) ->
            val mainData = questData.questInnerData
            val questMainID = mainData.innerQuestID
            val state = QuestStateUtil.stateToStr(questData.state)
            val fmq = questData.finishedList
            val fmqJson = Gson().toJson(fmq)
            tableQuest.update(
                Where.equals("uuid", uuid.toString()),
                Where.equals("questID", questID),
                Where.equals("questMainID", questMainID))
                .set("state", state)
                .set("finishedMainQuest", fmqJson)
                .run(source)
            updateOpen(uuid, mainData, questID, questMainID, "")
        }
    }

    private fun updateOpen(uuid: UUID, openData: QuestInnerData, questID: String, mainQuestID: String, subQuestID: String) {
        val state = QuestStateUtil.stateToStr(openData.state)
        val rewards = Gson().toJson(openData.rewardState)
        if (subQuestID.isEmpty()) {
            tableMainQuest.update(
                Where.equals("uuid", uuid.toString()),
                Where.equals("questID", questID),
                Where.equals("mainQuestID", mainQuestID))
                .set("state", state)
                .set("rewards", rewards)
                .run(source)
            openData.questSubList.forEach { (subID, subData) ->
                updateOpen(uuid, subData, questID, mainQuestID, subID)
            }
        }else {
            tableSubQuest.update(
                Where.equals("uuid", uuid.toString()),
                Where.equals("questID", questID),
                Where.equals("mainQuestID", mainQuestID),
                Where.equals("subQuestID", subQuestID))
                .set("state", state)
                .set("rewards", rewards)
                .run(source)
        }
        updateTarget(uuid, openData)
    }

    fun create(player: Player, questData: QuestData) {
        val uuid = player.uniqueId
        val questID = questData.questID
        val mainData = questData.questInnerData
        val questMainID = mainData.innerQuestID
        val state = QuestStateUtil.stateToStr(questData.state)
        val fmq = questData.finishedList
        val fmqJson = Gson().toJson(fmq)
        tableQuest.insert(uuid.toString(), questID, questMainID, state, fmqJson).run(source)
        createOpen(uuid, mainData, questID, questMainID, "")
    }

    private fun createOpen(uuid: UUID, openData: QuestInnerData, questID: String, mainQuestID: String, subQuestID: String) {
        val state = QuestStateUtil.stateToStr(openData.state)
        val rewards = Gson().toJson(openData.rewardState)
        if (subQuestID.isEmpty()) {
            tableMainQuest.insert(uuid.toString(), questID, mainQuestID, state, rewards).run(source)
            openData.questSubList.forEach { (subID, subData) ->
                createOpen(uuid, subData, questID, mainQuestID, subID)
            }
        }else {
            tableSubQuest.insert(uuid.toString(), questID, mainQuestID, subQuestID, state, rewards).run(source)
        }
        createTarget(uuid, openData)
    }

    private fun createTarget(uuid: UUID, openData: QuestInnerData) {
        openData.targetsData.forEach { (name, targetData) ->
            val questID = openData.questID
            val mainID = openData.innerQuestID
            val subID = openData.subQuestID
            val time = targetData.time
            val schedule = targetData.schedule
            tableTargets.insert(uuid.toString(), name, questID, mainID, subID, time, schedule).run(source)
        }
    }

    private fun updateTarget(uuid: UUID, openData: QuestInnerData) {
        openData.targetsData.forEach { (name, targetData) ->
            val questID = openData.questID
            val mainID = openData.innerQuestID
            val subID = openData.subQuestID
            val time = targetData.time
            val schedule = targetData.schedule
            tableTargets.update(
                Where.equals("uuid", uuid.toString()),
                Where.equals("name", name),
                Where.equals("questID", questID),
                Where.equals("mainQuestID", mainID),
                Where.equals("subQuestID", subID))
                .set("time", time)
                .set("schedule", schedule)
                .run(source)
        }
    }

    /*
        几乎错了

        需要修改

        接受S任务，完成，记录档案（状态）
        再次接受S任务

        所以要做个唯一ID
     */

    override fun removeQuestOpen(player: Player, questInnerData: QuestInnerData) {
        val uuid = player.uniqueId.toString()
        val questID = questInnerData.questID
        val mainID = questInnerData.innerQuestID
        val subID = questInnerData.subQuestID
        if (subID.isEmpty()) {
            tableMainQuest.delete(
                Where.equals("uuid", uuid),
                Where.equals("questID", questID),
                Where.equals("mainQuestID", mainID))
                .run(source)
        }else {
            tableMainQuest.delete(
                Where.equals("uuid", uuid),
                Where.equals("questID", questID),
                Where.equals("mainQuestID", mainID),
                Where.equals("subQuestID", subID))
                .run(source)
        }
        tableTargets.delete(
            Where.equals("uuid", uuid),
            Where.equals("questID", questID),
            Where.equals("mainQuestID", mainID),
            Where.equals("subQuestID", subID))
            .run(source)
    }

}