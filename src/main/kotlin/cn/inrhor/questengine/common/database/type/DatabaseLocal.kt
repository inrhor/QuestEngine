package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.QuestStateUtil
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class DatabaseLocal: Database() {

    fun getLocal(uuid: UUID): YamlConfiguration? {
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid")
        if (!file.exists()) return null
        return YamlConfiguration.loadConfiguration(file)
    }

    /*
        uuid.yml

        quest:
            questID:
                state: DOING
                finishedMainQuest: []
                main:
                    mainQuestID:
                        state: DOING
                        targets:
                            "name":
                                time: -1
                                schedule: 0
                        rewards:
                            rewardID:
                                has: false
                        sub:
                            subQuestID:
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
        val data = getLocal(uuid)?: return
        val questDataMap = mutableMapOf<String, QuestData>()
        if (data.contains("quest")) {
            data.getConfigurationSection("quest")!!.getKeys(false).forEach {
                val node = "quest.$it."
                val questID = it

                val nodeMain = node+"mainQuest."
                val mainQuestID = data.getString(nodeMain+"mainQuestID")?: return@forEach
                val mainState = QuestStateUtil.strToState(data.getString(nodeMain+"state")?: "IDLE")
                val mainModule = QuestManager.getMainQuestModule(questID, mainQuestID)?: return
                val mainTargetDataMap = returnTargetData(data, nodeMain, QuestManager.getMainModuleTargetMap(mainModule))
                val nodeSub = nodeMain+"subQuest."
                val questSubDataMap = mutableMapOf<String, QuestSubData>()

                for (subQuestID in data.getConfigurationSection(nodeSub+"targets")!!.getKeys(false)) {
                    val subState = QuestStateUtil.strToState(data.getString(nodeSub+"state")?: "IDLE")
                    val subModule = QuestManager.getSubQuestModule(questID, mainQuestID, subQuestID)?: return
                    val rewardSub = returnRewardData(data, nodeSub)
                    val subTargetDataMap = returnTargetData(data, nodeSub, QuestManager.getSubModuleTargetMap(subModule))
                    val questSubData = QuestSubData(questID, mainQuestID, subQuestID, subTargetDataMap, subState, rewardSub)
                    questSubDataMap[subQuestID] = questSubData
                }

                val finishedMain = data.getStringList(node+"finishedMainQuest")

                val rewardMain = returnRewardData(data, nodeMain)
                val questMainData = QuestMainData(questID, mainQuestID, questSubDataMap, mainTargetDataMap, mainState, rewardMain)

                val state = QuestStateUtil.strToState(data.getString(node+"state")?: "IDLE")

                val questData = QuestData(questID, questMainData, state, TeamManager.getTeamData(uuid), finishedMain)
                questDataMap[questID] = questData
            }
        }
    }

    private fun returnTargetData(data: YamlConfiguration, node: String, targetDataMap: MutableMap<String, TargetData>): MutableMap<String, TargetData> {
        for (name in data.getConfigurationSection(node+"targets")!!.getKeys(false)) {
            val nodeTarget = node+"targets.$name."
            val time = data.getInt(nodeTarget+"time")
            val scheduleMain = data.getInt(nodeTarget+"schedule")
            val targetData = targetDataMap[name]?: continue
            targetData.time = time
            targetData.schedule = scheduleMain
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
        val pData = DataStorage.getPlayerData(uuid)?: return
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid")
        if (!file.exists()) file.mkdir()
        val data = YamlConfiguration.loadConfiguration(file)
        pData.questDataList.forEach { (questID, questData) ->
            val state = QuestStateUtil.stateToStr(questData.state)
            val node = "quest.$questID."
            data.set(node+"state", state)
            val finishedMain = questData.finishedMainMap
            data.set(node+"finishedMainQuest", finishedMain)
            val mainData = questData.questMainData
            val mainID = mainData.mainQuestID
            val nodeMain = node+"main.$mainID."
            pushData(data, node+"main.$mainID.", mainData)
            mainData.questSubList.forEach { (subID, subData) ->
                pushData(data, nodeMain+"sub.$subID.", subData)
            }
        }
        data.save(file)
    }

    private fun pushData(data: YamlConfiguration, node: String, openData: QuestOpenData, ) {
        val state = QuestStateUtil.stateToStr(openData.state)
        data.set(node+"state", state)
        openData.rewardState.forEach { (rewardID, has) ->
            data.set(node+"rewards.$rewardID.has", has)
        }
        openData.targetsData.forEach { (name, targetData) ->
            val time = targetData.time
            val schedule = targetData.schedule
            data.set(node+"targets.$name.time", time)
            data.set(node+"targets.$name.schedule", schedule)
        }
    }

}