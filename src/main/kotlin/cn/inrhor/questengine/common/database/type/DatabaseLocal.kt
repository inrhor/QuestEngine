package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.toStr
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.common.quest.toState
import cn.inrhor.questengine.common.quest.toStr
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import taboolib.library.configuration.YamlConfiguration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DatabaseLocal: Database() {

    fun getLocal(uuid: UUID): YamlConfiguration {
        val data = File(QuestEngine.plugin.dataFolder, "data")
        if (!data.exists()) {
            data.mkdirs()
        }
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        if (!file.exists()) {
            file.createNewFile()
        }
        return YamlConfiguration.loadConfiguration(file)
    }

    override fun removeQuest(player: Player, questData: QuestData) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val questUUID = questData.questUUID
        data.set("quest.$questUUID", null)
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        data.save(file)
    }

    override fun removeControl(player: Player, controlID: String) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        data.set("control.$controlID", null)
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        data.save(file)
    }

    /*
        uuid.yml

        quest:
            UUID:
                questID: ""
                state: DOING
                finishedQuest: []
                innerQuest:
                    innerQuestID: ""
                    state: DOING
                    targets:
                        name: ""
                        time: -1
                        schedule: 0
                    rewards:
                        rewardID: ""
                        has: false
        control:
            controlID:
                priority: highest
                line: 0
                waitTime: 0

     */
    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val questDataMap = mutableMapOf<UUID, QuestData>()
        if (data.contains("quest")) {
            data.getConfigurationSection("quest").getKeys(false).forEach {
                val node = "quest.$it."
                val questUUID = UUID.fromString(it)
                val questID = data.getString(node+"questID")?: return@forEach

                val nodeInner = node+"innerQuest."

                val innerQuestID = data.getString(nodeInner+"innerQuestID")?: return@forEach
                val questInnerData = getInnerQuestData(data, nodeInner, player, questUUID, questID, innerQuestID)?: return@forEach

                val finished = data.getStringList(node+"finishedQuest")

                val state = (data.getString(node+"state")?: "IDLE").toState()

                val questData = QuestData(UUID.fromString(it), questID, questInnerData, state, TeamManager.getTeamData(uuid), finished)
                questDataMap[UUID.fromString(it)] = questData
                QuestManager.checkFailTime(player, questUUID, questID)
            }
        }
        if (data.contains("control")) {
            data.getConfigurationSection("control").getKeys(false).forEach {
                val node = "control.$it."
                val priority = data.getString(node+"priority")?: "normal"
                val line = data.getInt(node+"line")
                val waitTime = data.getInt(node+"waitTime")
                ControlManager.pullControl(player, it, priority, line, waitTime)
            }
        }
        DataStorage.getPlayerData(uuid).questDataList = questDataMap
    }

    override fun getInnerQuestData(player: Player, questUUID: UUID, questID: String, innerQuestID: String): QuestInnerData? {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val node = "quest.$questUUID.innerQuest."
        return getInnerQuestData(data, node, player, questUUID, questID, innerQuestID)
    }

    private fun getInnerQuestData(data: YamlConfiguration, node: String, player: Player, questUUID: UUID, questID: String, innerQuestID: String): QuestInnerData? {
        val rewardInner = returnRewardData(data, node)
        val innerState = (data.getString(node+"state")?: "IDLE").toState()
        val questModule = QuestManager.getQuestModule(questID)?: return null
        val innerModule = QuestManager.getInnerQuestModule(questID, innerQuestID)?: return null
        val innerTargetDataMap = returnTargets(
            player, questUUID,
            data, node, QuestManager.getInnerModuleTargetMap(questModule.modeType, innerModule))
        return QuestInnerData(questID, innerQuestID, innerTargetDataMap, innerState, rewardInner)
    }

    private fun returnTargets(player: Player, questUUID: UUID, data: YamlConfiguration, node: String, targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        if (!data.contains(node+"targets")) return targetDataMap
        for (name in data.getConfigurationSection(node+"targets").getKeys(false)) {
            val nodeTarget = node+"targets.$name."
            val targetData = targetDataMap[name]?: continue
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
        if (!data.contains(node+"rewards")) return rewardMap
        for (rewardID in data.getConfigurationSection(node+"rewards").getKeys(false)) {
            val nodeReward = node+"rewards.$rewardID."
            rewardMap[rewardID] = data.getBoolean(nodeReward+"has")
        }
        return rewardMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        if (!file.exists()) return
        val data = YamlConfiguration.loadConfiguration(file)
        pData.questDataList.forEach { (questUUID, questData) ->
            val state = questData.state.toStr()
            val node = "quest.$questUUID."
            val innerData = questData.questInnerData
            val innerID = innerData.innerQuestID
            data.set(node+"questID", questData.questID)
            data.set(node+"state", state)
            val finishedMain = questData.finishedList
            data.set(node+"finishedMainQuest", finishedMain)
            val innerNode = node+"innerQuest."
            data.set(innerNode+"innerQuestID", innerID)
            pushData(data, innerNode, innerData)
        }
        pData.controlData.highestControls.forEach { (cID, cData) ->
            pushControl(data, cID, cData)
        }
        pData.controlData.controls.forEach { (cID, cData) ->
            pushControl(data, cID, cData)
        }
        data.save(file)
    }

    private fun pushControl(data: YamlConfiguration, controlID: String, cData: QuestControlData) {
        val logType = ControlManager.runLogType(controlID, cData.controlPriority)
        if (logType == RunLogType.DISABLE) return
        val node = "control.$controlID."
        data.set(node+"priority", cData.controlPriority.toStr())
        when (logType) {
            RunLogType.RESTART -> {
                data.set(node+"line", 0)
                data.set(node+"waitTime", 0)
            }
            else -> {
                data.set(node+"line", cData.line)
                data.set(node+"waitTime", cData.waitTime)
            }
        }
    }

    private fun pushData(data: YamlConfiguration, node: String, questInnerData: QuestInnerData) {
        val state = questInnerData.state.toStr()
        data.set(node+"state", state)
        questInnerData.rewardState.forEach { (rewardID, has) ->
            data.set(node+"rewards.$rewardID.has", has)
        }
        questInnerData.targetsData.forEach { (name, targetData) ->
            val schedule = targetData.schedule
            data.set(node+"targets.$name.schedule", schedule)
            setTimeDate(data, node+"targets.$name.timeDate", targetData.timeDate)
            val endTimeDate = targetData.endTimeDate?: return@forEach
            setTimeDate(data, node + "targets.$name.endTimeDate", endTimeDate)
        }
    }

    private fun setTimeDate(data: YamlConfiguration, timeNode: String, date: Date) {
        if (!data.contains(timeNode)) {
            val dateStr = date.toStr()
            data.set(timeNode, dateStr)
        }
    }
}