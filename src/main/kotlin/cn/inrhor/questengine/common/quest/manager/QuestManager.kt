package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.QuestInnerModule
import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.database.type.DatabaseSQL
import cn.inrhor.questengine.common.database.type.DatabaseType
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.script.kether.KetherHandler
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.utlis.time.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang
import java.util.*

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    var questMap: HashMap<String, QuestModule> = LinkedHashMap()

    /**
     * 注册任务模块内容
     */
    fun register(questID: String, questModule: QuestModule) {
        questMap[questID] = questModule
    }

    /**
     * 得到任务模块内容
     */
    fun getQuestModule(questID: String): QuestModule? {
        return questMap[questID]
    }

    /**
     * 得到内部任务模块内容
     */
    fun getInnerQuestModule(questID: String, innerQuestID: String): QuestInnerModule? {
        val questModule = questMap[questID]?: return null
        questModule.innerQuestList.forEach {
            if (it.innerQuestID == innerQuestID) return it
        }
        return null
    }

    /**
     * 是否满足任务成员模式
     */
    fun matchQuestMode(questData: QuestData): Boolean {
        val questID = questData.questID
        val questModule = getQuestModule(questID)?: return false
        if (questModule.modeType == ModeType.PERSONAL) return true
        if (questModule.modeAmount <= 1) return true
        val tData = questData.teamData?: return false
        if (questModule.modeAmount >= TeamManager.getMemberAmount(tData)) return true
        return false
    }

    /**
     * 获取任务成员模式
     */
    fun getQuestMode(questID: String): ModeType {
        val questModule = getQuestModule(questID)?: return ModeType.PERSONAL
        return questModule.modeType
    }

    /**
     * 任务数据中是否存在 QuestID
     */
    fun existQuestData(player: Player, questID: String): Boolean {
        val pData = DataStorage.getPlayerData(player)
        pData.questDataList.values.forEach {
            if (it.questID == questID) return true
        }
        return false
    }

    /**
     * 接受任务
     */
    fun acceptQuest(player: Player, questID: String) {
        val pData = DataStorage.getPlayerData(player)
        val questModule = getQuestModule(questID) ?: return
        if (questModule.modeType == ModeType.COLLABORATION) {
            val tData = pData.teamData ?: return
            tData.members.forEach {
                val m = Bukkit.getPlayer(it) ?: return@forEach
                acceptQuest(m, questModule)
            }
            return
        }
        acceptQuest(player, questModule)
    }

    private fun acceptQuest(player: Player, questModule: QuestModule) {
        val startInnerQuest = questModule.getStartInnerQuest()?: return
        val questUUID = UUID.randomUUID()
        acceptInnerQuest(player, questUUID, questModule.questID, startInnerQuest, true)
    }

    /**
     * 接受下一个内部任务
     *
     * 前提是已接受任务
     */
    private fun acceptNextInnerQuest(player: Player, questUUID: UUID, questData: QuestData, innerQuestID: String) {
        val questID = questData.questID
        val questModule = getQuestModule(questID) ?: return
        if (questModule.modeType == ModeType.COLLABORATION) {
            val tData = questData.teamData?: return
            tData.members.forEach {
                val m = Bukkit.getPlayer(it)?: return@forEach
                nextInnerQuest(m, questUUID, questData, innerQuestID)
            }
            return
        }
        nextInnerQuest(player, questUUID, questData, innerQuestID)
    }

    private fun nextInnerQuest(player: Player, questUUID: UUID, questData: QuestData, innerQuestID: String) {
        val questID = questData.questID
        val questInnerModule = getInnerQuestModule(questID, innerQuestID) ?: return
        val nextInnerID = questInnerModule.nextInnerQuestID
        val nextInnerModule = getInnerQuestModule(questID, nextInnerID) ?: return
        acceptInnerQuest(player, questUUID, questID, nextInnerModule, false)
    }

    /**
     * 接受内部任务
     */
    fun acceptInnerQuest(player: Player, questData: QuestData, innerQuestID: String, isNewQuest: Boolean) {
        val questID = questData.questID
        val innerModule = getInnerQuestModule(questID, innerQuestID) ?: return
        acceptInnerQuest(player, questData.questUUID, questID, innerModule, isNewQuest)
    }

    private fun acceptInnerQuest(player: Player, questUUID: UUID, questID: String, innerQuestModule: QuestInnerModule, isNewQuest: Boolean) {
        val pData = DataStorage.getPlayerData(player)
        var state = QuestState.DOING
        if (isNewQuest && hasDoingInnerQuest(pData)) state = QuestState.IDLE
        val innerQuestID = innerQuestModule.innerQuestID
        val innerModule = getInnerQuestModule(questID, innerQuestID) ?: return
        val targetDataMap = mutableMapOf<String, TargetData>()
        innerModule.questTargetList.forEach { (name, target) ->
            val timeStr = target.time.lowercase(Locale.getDefault())
            val nowDate = Date()
            var endTime: Date? = null
            var timeUnit = "s"
            if (timeStr != "always") {
                val timeSpit = timeStr.split(" ")
                timeUnit = timeSpit[0]
                val time = timeSpit[1].toInt()
                when (timeUnit) {
                    "minute" -> endTime = TimeUtil.addDate(nowDate, Calendar.MINUTE, time)
                    "s" -> endTime = TimeUtil.addDate(nowDate, Calendar.SECOND, time)
                }
            }

            val targetData = TargetData(name, timeUnit, 0, target, nowDate, endTime)
            targetData.runTime(player, questUUID)
            targetDataMap[name] = targetData
        }
        val innerQuestData = QuestInnerData(questID, innerQuestID, targetDataMap, state)
        val questData = QuestData(questUUID, questID, innerQuestData, state, pData.teamData, mutableListOf())
        pData.questDataList[questUUID] = questData
        saveControl(player, pData, innerQuestData)
        runControl(pData, questID, innerQuestID)
        if (isNewQuest) {
            if (DatabaseManager.type == DatabaseType.MYSQL) {
                DatabaseSQL().create(player, questUUID, questData)
            }
        }
    }

    /**
     * 存储控制模块
     */
    fun saveControl(player: Player, pData: PlayerData, questInnerData: QuestInnerData) {
        if (questInnerData.state != QuestState.DOING) return
        val questID = questInnerData.questID
        val innerQuestID = questInnerData.innerQuestID
        val scriptList: MutableList<String>
        val controlID: String
        val mModule = getInnerQuestModule(questID, innerQuestID) ?: return
        val cModule = mModule.questControl
        controlID = cModule.controlID
        scriptList = cModule.scriptList
        if (controlID == "") return
        val controlData = QuestControlData(player, questInnerData, scriptList, 0, 0)
        pData.controlList[controlID] = controlData
    }

    fun generateControlID(questID: String, innerQuestID: String): String {
        return "[$questID]-[$innerQuestID]"
    }

    /**
     * 运行控制模块
     */
    fun runControl(player: Player, questID: String, innerQuestID: String) {
        val pData = DataStorage.getPlayerData(player)
        runControl(pData, questID, innerQuestID)
    }

    fun runControl(pData: PlayerData, questID: String, innerQuestID: String) {
        val id = generateControlID(questID, innerQuestID)
        val control = pData.controlList[id]?: return
        control.runScript()
    }

    /**
     * 检索是否已有处于 DOING 状态的内部任务
     */
    fun hasDoingInnerQuest(pData: PlayerData): Boolean {
        val questData = pData.questDataList
        questData.forEach { (_, u)->
            if (u.questInnerData.state == QuestState.DOING) return true
        }
        return false
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param runFailReward 如果失败，是否执行当前内部任务失败脚本
     */
    fun endQuest(player: Player, questData: QuestData, state: QuestState, runFailReward: Boolean) {
        questData.state = state
        if (state == QuestState.FAILURE && runFailReward) {
            val innerQuestID = questData.questInnerData.innerQuestID
            val failReward = getReward(questData.questID, innerQuestID, "", state) ?: return
            failReward.forEach {
                KetherHandler.eval(player, it)
            }
        }
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param runFailReward 如果失败，是否执行当前内部任务失败脚本
     */
    fun endQuest(player: Player, questUUID: UUID, state: QuestState, runFailReward: Boolean) {
        val questData = getQuestData(player, questUUID)?: return
        endQuest(player, questData, state, runFailReward)
    }

    /**
     * 结束当前内部任务，执行下一个内部任务或最终完成
     *
     * 最终完成请将 innerQuestID 设为 空
     */
    fun finishInnerQuest(player: Player, questUUID: UUID, questID: String, innerQuestID: String) {
        val questData = getQuestData(player, questUUID) ?: return
        val questInnerModule = getInnerQuestModule(questID, innerQuestID) ?: return
        val nextInnerID = questInnerModule.nextInnerQuestID
        if (nextInnerID == "") {
            questData.state = QuestState.FINISH
            val questModule = getQuestModule(questID)?: return
            if (questModule.modeType == ModeType.COLLABORATION) {
                val tData = questData.teamData?: return
                tData.members.forEach {
                    val m = Bukkit.getPlayer(it)?: return@forEach
                    val mQuestData = getQuestData(m, questUUID)?: return@forEach
                    mQuestData.state = QuestState.FINISH
                }
            }
        }else {
            acceptNextInnerQuest(player, questUUID, questData, nextInnerID)
        }
    }

    /**
     * 精准检索玩家任务数据
     */
    fun getQuestData(player: Player, questUUID: UUID): QuestData? {
        val pData = DataStorage.getPlayerData(player)
        return pData.questDataList[questUUID]
    }

    /**
     * 根据 QuestID 模糊检索玩家任务数据
     */
    fun getQuestData(uuid: UUID, questID: String): QuestData? {
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID) return it
        }
        return null
    }

    /**
     * 获得玩家当前内部任务数据
     */
    fun getInnerQuestData(player: Player, questUUID: UUID): QuestInnerData? {
        val questData = getQuestData(player, questUUID) ?: return null
        return questData.questInnerData
    }

    /**
     * 获得玩家内部任务数据
     */
    fun getInnerQuestData(player: Player, questUUID: UUID, innerQuestID: String): QuestInnerData? {
        val questData = getQuestData(player, questUUID) ?: return null
        return Database.database.getInnerQuestData(player, questUUID, questData.questID, innerQuestID)
    }

    /**
     * 得到奖励脚本，成功与否
     * 成功的一般是在目标完成时得到
     */
    fun getReward(questID: String, innerQuestID: String, rewardID: String, type: QuestState): MutableList<String>? {
        val questModule = questMap[questID]!!
        for (m in questModule.innerQuestList) {
            if (m.innerQuestID == innerQuestID) {
                return if (type == QuestState.FINISH) {
                    m.questReward.finishReward[rewardID]!!
                }else m.questReward.failReward
            }
        }
        return null
    }

    /**
     * 获得触发的内部任务目标
     */
    fun getDoingTarget(player: Player, name: String): QuestTarget? {
        val questData = getDoingQuest(player) ?: return null
        val innerData = questData.questInnerData
        val targetData = innerData.targetsData[name]?: return null
        return targetData.questTarget
    }

    /**
     * 得到内部任务内容的任务目标，交给数据
     *
     * 此为初始值，可许更新
     */
    fun getInnerModuleTargetMap(innerModule: QuestInnerModule): MutableMap<String, TargetData> {
        val targetDataMap = mutableMapOf<String, TargetData>()
        val date = Date()
        innerModule.questTargetList.forEach { (name, questTarget) ->
            val targetData = TargetData(name, TimeUtil.timeUnit(questTarget), 0, questTarget, date, null)
            targetDataMap[name] = targetData
        }
        return targetDataMap
    }

    /**
     * 获得正在进行中的任务
     */
    fun getDoingQuest(player: Player): QuestData? {
        val pData = DataStorage.getPlayerData(player)
        if (pData.questDataList.isEmpty()) return null
        pData.questDataList.values.forEach {
            if (it.state == QuestState.DOING) {
                return it
            }
        }
        return null
    }

    /**
     * 放弃和清空任务数据
     */
    fun quitQuest(player: Player, questID: String) {
        val uuid = player.uniqueId
        val questData = getQuestData(uuid, questID)?: return run {
            player.sendLang("QUEST.NULL_QUEST_DATA", questID) }
        val questModule = getQuestModule(questID)?: return
        val questUUID = questData.questUUID
        val pData = DataStorage.getPlayerData(uuid)
        val questList = pData.questDataList
        if (questModule.modeType == ModeType.COLLABORATION) {
            val tData = questData.teamData?: run { questList.remove(questUUID); return }
            tData.members.forEach {
                if (uuid == it) return@forEach
                val mData = DataStorage.getPlayerData(it)
                val mQuestList = mData.questDataList
                if (!mQuestList.containsKey(questUUID)) return@forEach
                val m = Bukkit.getPlayer(it)?: return@forEach
                val mQuestData = getQuestData(uuid, questID)?: return@forEach
                databaseRemoveInner(m, mQuestList, questUUID, mQuestData.questInnerData)
                databaseRemoveQuest(m, mQuestData)
            }
        }
        databaseRemoveInner(player, questList, questUUID, questData.questInnerData)
        databaseRemoveQuest(player, questData)
    }

    private fun databaseRemoveQuest(player: Player, questData: QuestData) {
        Database.database.removeQuest(player, questData)
    }

    private fun databaseRemoveInner(player: Player, questList: MutableMap<UUID, QuestData>, questUUID: UUID, questInnerData: QuestInnerData) {
        Database.database.removeInnerQuest(player, questUUID, questInnerData)
        questList.remove(questUUID)
    }

}