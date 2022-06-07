package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.common.quest.enum.toState
import cn.inrhor.questengine.common.quest.enum.toStr
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import taboolib.module.configuration.Configuration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DatabaseLocal: Database() {

    fun getLocal(uuid: UUID): Configuration {
        val data = File(QuestEngine.plugin.dataFolder, "data")
        if (!data.exists()) {
            data.mkdirs()
        }
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        if (!file.exists()) {
            file.createNewFile()
        }
        return Configuration.loadFromFile(file)
    }

    override fun removeQuest(player: Player, questData: GroupData) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val questUUID = questData.uuid
        data["quest.$questUUID"] = null
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        data.saveToFile(file)
    }

    override fun removeControl(player: Player, controlID: String) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        data["control.$controlID"] = null
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        data.saveToFile(file)
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val questDataMap = mutableMapOf<UUID, GroupData>()
        if (data.contains("quest")) {
            data.getConfigurationSection("quest")!!.getKeys(false).forEach {
                val node = "quest.$it."
                val questUUID = UUID.fromString(it)
                val questID = data.getString(node+"questID")?: return@forEach

                val nodeInner = node+"innerQuest."

                val innerQuestID = data.getString(nodeInner+"innerQuestID")?: return@forEach
                val questInnerData = getInnerQuestData(data, nodeInner, questUUID, innerQuestID)?: return@forEach
                val finished = data.getStringList(node+"finishedQuest").toMutableSet()

                val state = (data.getString(node+"state")?: "IDLE").toState()

                val questData = GroupData(UUID.fromString(it), questID,
                    questInnerData, state, finished)
                questDataMap[UUID.fromString(it)] = questData
                QuestManager.checkTimeTask(player, questUUID, questID)
            }
        }
        if (data.contains("control")) {
            data.getConfigurationSection("control")!!.getKeys(false).forEach {
                val node = "control.$it."
                val line = data.getInt(node+"line")
                ControlManager.pullControl(player, it, line)
            }
        }
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList = questDataMap
        TargetManager.runTask(pData, player)
        data.getStringList("tags").forEach {
            pData.tagsData.addTag(it)
        }
    }

    override fun getInnerQuestData(player: Player, questUUID: UUID, innerQuestID: String): QuestData? {
        val uuid = player.uniqueId
        val data = getLocal(uuid)
        val node = "quest.$questUUID.innerQuest."
        return getInnerQuestData(data, node, questUUID, innerQuestID)
    }

    private fun getInnerQuestData(data: Configuration, node: String, questUUID: UUID, innerQuestID: String): QuestData? {
        val innerState = (data.getString(node+"state")?: "IDLE").toState()
        val questID = data.getString("quest.$questUUID.questID")?: return null
        val innerModule = QuestManager.getInnerModule(questID, innerQuestID)?: return null
        val innerTargetDataMap = returnTargets(
            data, node, QuestManager.getInnerModuleTargetMap(questUUID, innerModule))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timeDate = if (data.contains(node+"timeDate")) dateFormat.parse(data.getString(node+"timeDate")) else Date()
        val end = if (data.contains(node+"endTimeDate")) dateFormat.parse(data.getString(node+"endTimeDate")) else null
        return QuestData(questID, innerQuestID, innerTargetDataMap, innerState, timeDate, end)
    }

    private fun returnTargets(data: Configuration, node: String, targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        if (!data.contains(node+"targets")) return targetDataMap
        for (id in data.getConfigurationSection(node+"targets")!!.getKeys(false)) {
            val nodeTarget = node+"targets.$id."
            val targetData = targetDataMap[id]?: continue
            targetData.schedule  = data.getInt(nodeTarget+"schedule")
            targetDataMap[id] = targetData
        }
        return targetDataMap
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid.yml")
        if (!file.exists()) return
        val data = Configuration.loadFromFile(file)
        pData.questDataList.forEach { (questUUID, questData) ->
            val state = questData.state.toStr()
            val node = "quest.$questUUID."
            val innerData = questData.questInnerData
            val innerID = innerData.innerQuestID
            data[node+"questID"] = questData.questID
            data[node+"state"] = state
            val finishedMain = questData.finishedList
            data[node+"finishedMainQuest"] = finishedMain
            val innerNode = node+"innerQuest."
            data[innerNode+"innerQuestID"] = innerID
            pushData(data, innerNode, innerData)
        }
        pData.controlQueue.highestControls.forEach { (cID, cData) ->
            pushControl(data, cID, cData)
        }
        pData.controlQueue.controls.forEach { (cID, cData) ->
            pushControl(data, cID, cData)
        }
        data["tags"] = pData.tagsData.getList()
        data.saveToFile(file)
    }

    private fun pushControl(data: Configuration, controlID: String, cData: ControlData) {
        val logType = ControlManager.runLogType(controlID)
        if (logType == RunLogType.DISABLE) return
        val node = "control.$controlID."
        data[node+"priority"] = cData.priority.toString()
        when (logType) {
            RunLogType.RESTART -> {
                data[node+"line"] = 0
                data[node+"waitTime"] = 0
            }
            else -> {
                data[node+"line"] = cData.line
            }
        }
    }

    private fun pushData(data: Configuration, node: String, questInnerData: QuestData) {
        val state = questInnerData.state.toStr()
        data[node+"state"] = state
        questInnerData.target.forEach { (id, targetData) ->
            val schedule = targetData.schedule
            data[node+"targets.$id.schedule"] = schedule
        }
        setTimeDate(data, node+"timeDate", questInnerData.timeDate)
        val endTimeDate = questInnerData.endDate?: return
        setTimeDate(data, node + "endTimeDate", endTimeDate)
    }

    private fun setTimeDate(data: Configuration, timeNode: String, date: Date) {
        if (!data.contains(timeNode)) {
            val dateStr = date.toStr()
            data[timeNode] = dateStr
        }
    }
}