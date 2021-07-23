package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
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
        SQLColumnType.VARCHAR.toColumn(256, "finishedMainQuest").columnOptions(SQLColumnOption.KEY)
    )

    val tableMainQuest = SQLTable(
        table+"_user_main_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36,"state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256,"rewards").columnOptions(SQLColumnOption.KEY)
    )

    val tableSubQuest = SQLTable(
        table+"_user_sub_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "subQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "state").columnOptions(SQLColumnOption.KEY),
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
            Where.equals("uuid", uuid))
            .row("questID")
            .row("questMainID")
            .row("state")
            .row("finishedMainQuest")
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
            Where.equals("uuid", uuid),
            Where.equals("questID", questID),
            Where.equals("mainQuestID", mainQuestID))
            .row("state")
            .row("rewards")
            .to(source)
            .map {
                it.getString("state") to it.getString("targets") to it.getString("rewards")
            }.forEach {
                val mainModule = QuestManager.getMainQuestModule(questID, mainQuestID)
                if (mainModule != null) {
                    val targets = returnTargets(
                        uuid, questID, mainQuestID, "",
                        QuestManager.getMainModuleTargetMap(mainModule)
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
            Where.equals("uuid", uuid),
            Where.equals("questID", questID),
            Where.equals("mainQuestID", mainQuestID))
            .row("subQuestID")
            .row("state")
            .row("rewards")
            .to(source)
            .map {
                it.getString("subQuestID") to it.getString("state") to it.getString("rewards")
            }.forEach {
                val mainModule = QuestManager.getMainQuestModule(questID, mainQuestID)
                if (mainModule != null) {
                    val subQuestID = it.first.first
                    val targets = returnTargets(uuid, questID, mainQuestID, subQuestID,
                        QuestManager.getMainModuleTargetMap(mainModule))
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
            Where.equals("uuid", uuid),
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
                val targetData = targetDataMap[name]
                if (targetData != null) {
                    targetData.time = it.first.second
                    targetData.schedule = it.second
                }
            }
        return targetDataMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.forEach { (questID, questData) ->
            val mainData = questData.questMainData
            val questMainID = mainData.mainQuestID
            val state = QuestStateUtil.stateToStr(questData.state)
            val fmq = questData.finishedMainList
            val fmqJson = Gson().toJson(fmq)
            tableQuest.insert(uuid, questID, questMainID, state, fmqJson).run(source)
            pushOpen(uuid, mainData, questID, questMainID, "")
        }
    }

    private fun pushOpen(uuid: UUID, openData: QuestOpenData, questID: String, mainQuestID: String, subQuestID: String) {
        val state = QuestStateUtil.stateToStr(openData.state)
        val rewards = Gson().toJson(openData.rewardState)
        if (subQuestID.isEmpty()) {
            tableMainQuest.insert(uuid, questID, mainQuestID, state, rewards)
            openData.questSubList.forEach { (subID, subData) ->
                pushOpen(uuid, subData, questID, mainQuestID, subID)
            }
        }else {
            tableSubQuest.insert(uuid, questID, mainQuestID, subQuestID, state, rewards)
        }
        pushTarget(uuid, openData)
    }

    fun pushTarget(uuid: UUID, openData: QuestOpenData) {
        openData.targetsData.forEach { (name, targetData) ->
            val questID = openData.questID
            val mainID = openData.mainQuestID
            val subID = openData.subQuestID
            val time = targetData.time
            val schedule = targetData.schedule
            tableTargets.insert(uuid, name, questID, mainID, subID, time, schedule)
        }
    }


}