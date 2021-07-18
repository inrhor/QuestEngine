package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestMainData
import cn.inrhor.questengine.common.database.data.quest.QuestSubData
import cn.inrhor.questengine.common.database.data.quest.TargetData
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
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questMainData").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256, "finishedMainQuest").columnOptions(SQLColumnOption.KEY)
    )

    val tableMainQuest = SQLTable(
        table+"_user_main_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36,"state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256,"rewards").columnOptions(SQLColumnOption.KEY)
    )

    val tableSubQuest = SQLTable(
        table+"_user_sub_quest",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
        SQLColumnType.VARCHAR.toColumn(36, "questID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "mainQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "subQuestID").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(36, "state").columnOptions(SQLColumnOption.KEY),
        SQLColumnType.VARCHAR.toColumn(256,"rewards").columnOptions(SQLColumnOption.KEY)
    )

    val tableTargets = SQLTable(
        table+"_user_targets",
        SQLColumnType.VARCHAR.toColumn(36, "uuid").columnOptions(SQLColumnOption.UNIQUE_KEY),
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
        val pData = DataStorage.getPlayerData(uuid)?: return
        tableQuest.select(
            Where.equals("uuid", uuid))
            .row("questID")
            .row("questMainData")
            .row("state")
            .row("finishedMainQuest")
            .to(source)
            .map {
                it.getString("questID") to it.getString("questMainData") to it.getString("state") to it.getString("finishedMainQuest")
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

    /*fun l() {
        val uuid = *//*player.uniqueId*//*"haha"
        tableQuest.select(Where.equals("uuid", uuid))
            .row("uuid")
            .row("questID")
            .row("finishedMainQuest")
            .to(source)
            .map {
                it.getString("uuid") to it.getString("finishedMainQuest") to it.getString("questID")
            }.forEach {
                MsgUtil.send("Mysql - Test: 1>  "+it.first+"   2>  "+it.first.second+"   3>  "+it.second)
                val s = it.first.second
                val json = Gson().fromJson(s, MutableList::class.java)
                json.forEach { i ->
                    MsgUtil.send("json  $i")
                }
            }
    }*/

    /*private fun mainQuestData(uuid: UUID, questID: String): QuestMainData {
        tableMainQuest.select(Where.equals("uuid", uuid), Where.equals("questID", questID))
            .row("mainQuestID")
            .to(source)
            .map {
                it.getString("mainQuestID") to it.getString("state") to it.getString("targets") to it.getString("rewards")
            }.forEach {
                return QuestMainData(questID, it.first, mu)
            }
    }*/

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)?: return
        pData.questDataList.forEach { (questID, questData) ->
            val state = QuestStateUtil.stateToStr(questData.state)
        }
    }


}