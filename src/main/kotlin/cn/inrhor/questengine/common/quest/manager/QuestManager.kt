package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.control.ControlPriority
import cn.inrhor.questengine.api.quest.QuestInnerModule
import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.script.kether.eval
import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.script.kether.evalBooleanSet
import cn.inrhor.questengine.utlis.time.add
import cn.inrhor.questengine.utlis.time.toDate
import cn.inrhor.questengine.utlis.time.toTimeUnit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.platform.util.sendLang
import java.util.*

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    var questMap = mutableMapOf<String, QuestModule>()

    /**
     * 自动接受的任务模块内容
     */
    var autoQuestMap = mutableMapOf<String, QuestModule>()

    /**
     * 注册任务模块内容
     */
    fun register(questID: String, questModule: QuestModule) {
        questMap[questID] = questModule
        if (questModule.acceptWay.lowercase() == "auto") {
            autoQuestMap[questID] = questModule
        }
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
    fun existQuestData(uuid: UUID, questID: String): Boolean {
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID) return true
        }
        return false
    }

    /**
     * 任务数据中是否存在 QuestID 及其状态
     */
    fun existQuestData(uuid: UUID, questID: String, state: QuestState): Boolean {
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID && it.state == state) return true
        }
        return false
    }

    /**
     * 任务数据中 QuestID 存在数量
     */
    fun questDataAmount(uuid: UUID, questID: String): Int {
        var amount = 0
        val pData = DataStorage.getPlayerData(uuid)
        pData.questDataList.values.forEach {
            if (it.questID == questID) amount++
        }
        return amount
    }

    /**
     * 接受任务
     */
    fun acceptQuest(player: Player, questID: String) {
        val pData = DataStorage.getPlayerData(player)
        val questModule = getQuestModule(questID) ?: return
        if (questModule.modeType == ModeType.COLLABORATION) {
            val tData = pData.teamData ?: return
            if (!acceptCondition(tData.playerMembers(), questID)) return
                tData.members.forEach {
                val m = Bukkit.getPlayer(it) ?: return@forEach
                acceptQuest(m, questModule)
            }
            return
        }
        if (!acceptCondition(mutableSetOf(player), questID)) return
        acceptQuest(player, questModule)
    }

    private fun acceptQuest(player: Player, questModule: QuestModule) {
        val startInnerQuest = questModule.getStartInnerQuest()?: return
        val questUUID = UUID.randomUUID()
        checkFailTime(player, questUUID, questModule)
        acceptInnerQuest(player, questUUID, questModule.questID, startInnerQuest, true)
    }

    fun passMaxQuantity(players: MutableSet<Player>, questModule: QuestModule): Boolean {
        val max = questModule.maxQuantity
        if (max < 0) return true
        players.forEach {
            if (questDataAmount(it.uniqueId, questModule.questID) >= max) return false
        }
        return true
    }

    /**
     * 接受任务检查是否通过条件
     */
    fun acceptCondition(players: MutableSet<Player>, questID: String): Boolean {
        val questModule = getQuestModule(questID) ?: return false
        if (!passMaxQuantity(players, questModule)) return false
        val check = questModule.acceptCheck
        val c = questModule.acceptCondition
        if (check <= 0) {
            return evalBooleanSet(players, c)
        }
        val list = mutableListOf<String>()
        var i = 0
        c.forEach {
            list.add(it)
            i++
            if (i >= check) return@forEach
        }
        return evalBooleanSet(players, list)
    }

    /**
     * 接受任务后开始使用调度器检查条件，一旦不符合将失败
     *
     * 属于自动化模块
     */
    fun checkFailTime(player: Player, questUUID: UUID, questID: String) {
        val questModule = getQuestModule(questID)?: return
        checkFailTime(player, questUUID, questModule)
    }

    /**
     * 接受任务后开始使用调度器检查条件，一旦不符合将失败
     *
     * 属于自动化模块
     */
    fun checkFailTime(player: Player, questUUID: UUID, questModule: QuestModule) {
        val list = mutableListOf<String>()
        val check = questModule.failCheck
        val c = questModule.failCondition
        var i = 0
        c.forEach {
            list.add(it)
            i++
            if (i >= check) return@forEach
        }
        submit(async = true, period = 10L) {
            if (!player.isOnline) return@submit
            if (!evalBoolean(player, list)) {
                val modeType = questModule.modeType
                endQuest(player, modeType, questUUID, QuestState.FAILURE, false)
                runFailTime(player, modeType, questModule.failKether)
                return@submit
            }
            val qData = getQuestData(player, questUUID)?: return@submit
            if (qData.state == QuestState.FAILURE) return@submit
        }
    }

    private fun runFailTime(player: Player, modeType: ModeType, failKether: MutableList<String>) {
        val pData = DataStorage.getPlayerData(player)
        val tData = pData.teamData
        if (modeType == ModeType.COLLABORATION && tData != null) {
            tData.playerMembers().forEach {
                eval(it, failKether)
            }
            return
        }
        eval(player, failKether)
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
        val innerModule = getInnerQuestModule(questID, innerQuestID)?: return
        val questModule = getQuestModule(questID)?: return
        val targetDataMap = mutableMapOf<String, TargetData>()
        innerModule.questTargetList.forEach { (name, target) ->
            val timeStr = target.time.lowercase()
            val nowDate = Date()
            var endTime: Date? = null
            var timeUnit = "s"
            if (timeStr != "always") {
                val timeSpit = timeStr.split(" ")
                timeUnit = timeSpit[0]
                when (timeUnit) {
                    "minute" -> {
                        endTime = nowDate.add(Calendar.MINUTE, timeSpit[1].toInt())
                    }
                    "s" -> {
                        endTime = nowDate.add(Calendar.SECOND, timeSpit[1].toInt())
                    }
                    "date" -> endTime = timeSpit[1].toDate()
                }
            }
            val targetData = TargetData(name, timeUnit, 0, target, nowDate, endTime, questModule.modeType)
            targetData.runTime(player, questUUID)
            targetDataMap[name] = targetData
        }
        val innerQuestData = QuestInnerData(questID, innerQuestID, targetDataMap, state)
        val questData = QuestData(questUUID, questID, innerQuestData, state, pData.teamData, mutableListOf())
        pData.questDataList[questUUID] = questData
        ControlManager.saveControl(player, pData, innerQuestData)
        if (isNewQuest) {
            Database.database.createQuest(player, questUUID, questData)
        }
    }

    /**
     * 设置任务状态，包括内部任务
     */
    fun setQuestState(player: Player, questData: QuestData, state: QuestState) {
        if (state == QuestState.DOING) {
            val pData = DataStorage.getPlayerData(player)
            pData.questDataList.values.forEach {
                if (it.state == state) setQuestState(player, it, state)
            }
        }
        questData.state = state
        questData.questInnerData.state = state
    }

    /**
     * 检索是否已有处于 DOING 状态的内部任务
     */
    fun hasDoingInnerQuest(pData: PlayerData): Boolean {
        return hasStateInnerQuest(pData, QuestState.DOING)
    }

    /**
     * 检索是否已有处于某状态的内部任务
     */
    fun hasStateInnerQuest(pData: PlayerData, state: QuestState): Boolean {
        val questData = pData.questDataList
        questData.values.forEach {
            if (it.questInnerData.state == state) return true
        }
        return false
    }

    /**
     * 检索是否已有处于某状态的内部任务
     */
    fun hasStateInnerQuest(player: Player, state: QuestState): Boolean {
        return hasStateInnerQuest(DataStorage.getPlayerData(player), state)
    }

    /**
     * 查看内部任务的状态是否为所求
     */
    fun isStateInnerQuest(player: Player, questUUID: UUID, state: QuestState): Boolean {
        val qData = getQuestData(player, questUUID)?: return false
        return qData.questInnerData.state == state
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param innerFailReward 如果失败，是否执行当前内部任务失败脚本
     */
    private fun endQuest(player: Player, questData: QuestData, state: QuestState, innerFailReward: Boolean) {
        questData.state = state
        val innerData = questData.questInnerData
        innerData.state = state
        if (state == QuestState.FAILURE && innerFailReward) {
            val innerQuestID = innerData.innerQuestID
            val failReward = getReward(questData.questID, innerQuestID, "", state) ?: return
            eval(player, failReward)
        }
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param innerFailReward 如果失败，是否执行当前内部任务失败脚本
     */
    fun endQuest(player: Player, modeType: ModeType, questUUID: UUID, state: QuestState, innerFailReward: Boolean) {
        val pData = DataStorage.getPlayerData(player)
        val tData = pData.teamData
        if (modeType == ModeType.COLLABORATION && tData != null) {
            tData.playerMembers().forEach {
                val qData = getQuestData(player, questUUID)
                if (qData != null) {
                    endQuest(it, qData, state, innerFailReward)
                }
            }
            return
        }
        val qData = getQuestData(player, questUUID)?: return
        endQuest(player, qData, state, innerFailReward)
    }

    /**
     * 结束当前内部任务，执行下一个内部任务或最终完成
     *
     * 最终完成请将 innerQuestID 设为 空
     */
    fun finishInnerQuest(player: Player, questUUID: UUID, questID: String, innerQuestID: String) {
        val questData = getQuestData(player, questUUID) ?: return
        val innerData = questData.questInnerData
        innerData.state = QuestState.FINISH
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
    fun getInnerModuleTargetMap(modeType: ModeType, innerModule: QuestInnerModule): MutableMap<String, TargetData> {
        val targetDataMap = mutableMapOf<String, TargetData>()
        val date = Date()
        innerModule.questTargetList.forEach { (name, questTarget) ->
            val targetData = TargetData(name, questTarget.time.toTimeUnit(), 0,
                questTarget, date, null, modeType)
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
            player.sendLang("QUEST-NULL_QUEST_DATA", questID) }
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
                val mControl = mData.controlData
                val m = Bukkit.getPlayer(it)?: return@forEach
                val mQuestData = getQuestData(uuid, questID)?: return@forEach
                databaseRemove(m, questUUID, mQuestData, mControl)
                mQuestList.remove(questUUID)
            }
        }
        databaseRemove(player, questUUID, questData, pData.controlData)
        questList.remove(questUUID)
    }

    private fun databaseRemove(player: Player,
                               questUUID: UUID,
                               questData: QuestData, controlData: ControlData) {
        databaseRemoveControl(player, questData.questID, controlData)
        databaseRemoveInner(player, questUUID)
        databaseRemoveQuest(player, questData)
    }

    private fun databaseRemoveQuest(player: Player, questData: QuestData) {
        Database.database.removeQuest(player, questData)

    }

    private fun databaseRemoveInner(player: Player, questUUID: UUID) {
        Database.database.removeInnerQuest(player, questUUID)
    }

    private fun databaseRemoveControl(player: Player, questID: String, controlData: ControlData) {
        val pData = DataStorage.getPlayerData(player)
        pData.controlData.controls.values.forEach {
            if (it.controlID.startsWith("$questID-")) {
                Database.database.removeControl(player, it.controlID)
                controlData.highestControls.remove(it.controlID)
            }
        }
        pData.controlData.highestControls.values.forEach {
            if (it.controlID.startsWith("$questID-")) {
                Database.database.removeControl(player, it.controlID)
                controlData.controls.remove(it.controlID)
            }
        }
    }

}