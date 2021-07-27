package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.QuestStateUtil
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DatabaseLocal: Database() {

    fun getLocal(uuid: UUID): YamlConfiguration {
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid")
        if (!file.exists()) {
            file.mkdirs()
        }
        return YamlConfiguration.loadConfiguration(file)
    }

    override fun removeQuest(player: Player, questData: QuestData) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val questUUID = questData.questUUID
        data.set("quest.$questUUID", null)
    }

    override fun removeInnerQuest(player: Player, questUUID: UUID, questInnerData: QuestInnerData) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        data.set("quest.$questUUID.innerQuest."+questInnerData.innerQuestID, null)
    }

    /*
        uuid.yml

        quest:
            UUID:
                questID: ""
                state: DOING
                finishedQuest: []
                innerQuest:
                    innerQuestID:
                        state: DOING
                        targets:
                            "name":
                            time: -1
                            schedule: 0
                        rewards:
                            rewardID:
                            has: false

     */
    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val questDataMap = mutableMapOf<UUID, QuestData>()
        if (data.contains("quest")) {
            data.getConfigurationSection("quest")!!.getKeys(false).forEach {
                val node = "quest.$it."
                val questUUid = UUID.fromString(it)
                val questID = data.getString(node+"questID")?: return@forEach

                val nodeInner = node+"innerQuest."
                val innerQuestID = data.getString(nodeInner+"innerQuestID")?: return@forEach
                val innerState = QuestStateUtil.strToState(data.getString(nodeInner+"state")?: "IDLE")
                val innerModule = QuestManager.getInnerQuestModule(questID, innerQuestID)?: return
                val innerTargetDataMap = returnTargets(
                    player, questUUid,
                    data, nodeInner, QuestManager.getInnerModuleTargetMap(innerModule))

                val finished = data.getStringList(node+"finishedQuest")

                val rewardInner = returnRewardData(data, nodeInner)
                val questInnerData = QuestInnerData(questID, innerQuestID, innerTargetDataMap, innerState, rewardInner)

                val state = QuestStateUtil.strToState(data.getString(node+"state")?: "IDLE")

                val questData = QuestData(UUID.fromString(it), questID, questInnerData, state, TeamManager.getTeamData(uuid), finished)
                questDataMap[UUID.fromString(it)] = questData
            }
        }
        DataStorage.getPlayerData(uuid).questDataList = questDataMap
    }

    private fun returnTargets(player: Player, questUUID: UUID, data: YamlConfiguration, node: String, targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        for (name in data.getConfigurationSection(node+"targets")!!.getKeys(false)) {
            val nodeTarget = node+"targets.$name."
            val targetData = targetDataMap[name]?: continue
//            targetData.time = data.getInt(nodeTarget+"time")
            targetData.schedule  = data.getInt(nodeTarget+"schedule")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timeDate = dateFormat.parse(data.getString(nodeTarget+"timeDate"))
            targetData.timeDate = timeDate
            val endTimeDate = dateFormat.parse(data.getString(nodeTarget+"endTimeDate"))
            targetData.endTimeDate = endTimeDate
            targetDataMap[name] = targetData
            targetData.runTime(player, questUUID)
        }
        return targetDataMap
    }

    private fun returnRewardData(data: YamlConfiguration, node: String): MutableMap<String, Boolean> {
        val rewardMap = mutableMapOf<String, Boolean>()
        for (rewardID in data.getConfigurationSection(node+"rewards")!!.getKeys(false)) {
            val nodeReward = node+"rewards.$rewardID."
            rewardMap[rewardID] = data.getBoolean(nodeReward+"has")
        }
        return rewardMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid")
        if (!file.exists()) file.mkdirs()
        val data = YamlConfiguration.loadConfiguration(file)
        pData.questDataList.forEach { (questUUID, questData) ->
            val state = QuestStateUtil.stateToStr(questData.state)
            val node = "quest.$questUUID."
            data.set(node+"questID", questData.questID)
            data.set(node+"state", state)
            val finishedMain = questData.finishedList
            data.set(node+"finishedMainQuest", finishedMain)
            val innerData = questData.questInnerData
            val innerID = innerData.innerQuestID
            pushData(data, node+"innerQuest.$innerID.", innerData)
        }
        data.save(file)
    }

    private fun pushData(data: YamlConfiguration, node: String, questInnerData: QuestInnerData) {
        val state = QuestStateUtil.stateToStr(questInnerData.state)
        data.set(node+"state", state)
        questInnerData.rewardState.forEach { (rewardID, has) ->
            data.set(node+"rewards.$rewardID.has", has)
        }
        questInnerData.targetsData.forEach { (name, targetData) ->
//            val time = targetData.time
            val schedule = targetData.schedule
//            data.set(node+"targets.$name.time", time)
            data.set(node+"targets.$name.schedule", schedule)
            setTimeDate(data, node+"targets.$name.timeDate", targetData.timeDate)
            val endTimeDate = targetData.endTimeDate?: return@forEach
            setTimeDate(data, node + "targets.$name.endTimeDate", endTimeDate)
        }
    }

    private fun setTimeDate(data: YamlConfiguration, timeNode: String, date: Date) {
        if (!data.contains(timeNode)) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateStr = dateFormat.format(date)
            data.set("$timeNode.timeDate", dateStr)
        }
    }
}