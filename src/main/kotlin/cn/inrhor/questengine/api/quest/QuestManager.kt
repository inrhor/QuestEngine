package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.script.kether.KetherHandler
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.TargetSubData
import org.bukkit.entity.Player
import java.util.HashMap
import java.util.LinkedHashMap

object QuestManager {

    /**
     * 注册的任务模块内容
     */
    private var questMap: HashMap<String, QuestModule> = LinkedHashMap()

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
     * 得到主线任务模块内容
     */
    fun getMainQuestModule(questID: String, mainQuestID: String): QuestMainModule? {
        val questModule = questMap[questID]?: return null
        questModule.mainQuestList.forEach {
            if (it.mainQuestID == mainQuestID) return it
        }
        return null
    }

    /**
     * 得到支线任务模块内容
     */
    fun getSubQuestModule(questID: String, mainQuestID: String, subQuestID: String): QuestSubModule? {
        val questModule = questMap[questID]?: return null
        questModule.mainQuestList.forEach {
            if (it.mainQuestID == mainQuestID) {
                it.subQuestList.forEach { i ->
                    if (i.subQuestID == subQuestID) return i
                }
            }
        }
        return null
    }

    /**
     * 接受任务
     */
    fun acceptQuest(player: Player, questID: String) {
        val pData = DataStorage.getPlayerData(player)?: return
        val questModule = getQuestModule(questID)?: return
        val startMainQuest = questModule.getStartMainQuest()?: return
        acceptMainQuest(player, questID, startMainQuest, true)
    }

    /**
     * 接受下一个主线任务
     *
     * 前提是已接受任务
     */
    fun acceptNextMainQuest(player: Player, questData: QuestData, mainQuestID: String) {
        val questID = questData.questID
        val questMainModule = getMainQuestModule(questID, mainQuestID)?: return
        val nextMainID = questMainModule.nextMinQuestID
        val nextMainModule = getMainQuestModule(questID, nextMainID)?: return
        acceptMainQuest(player, questID, nextMainModule, false)
    }

    /**
     * 接受支线任务，前提是已接受了所在的主线任务
     */
    fun acceptSubQuest(player: Player, questID: String, mainQuestID: String, subQuestID: String) {
        val mainData = getMainQuestData(player, questID)?: return
        if (mainData.mainQuestID != mainQuestID) return
        val subData = mainData.questSubList[subQuestID]?: return
        subData.state = QuestState.DOING
        val pData = DataStorage.getPlayerData(player)?: return
        saveControl(player, pData, subData)
        runControl(pData, questID, mainQuestID, subQuestID)
    }

    private fun acceptMainQuest(player: Player, questID: String, mainQuest: QuestMainModule, isNewQuest: Boolean) {
        val pData = DataStorage.getPlayerData(player)?: return
        var state = QuestState.DOING
        if (isNewQuest && hasDoingMainQuest(pData)) state = QuestState.IDLE
        val subQuestDataList = mutableMapOf<String, QuestSubData>()
        val mainTargetList = mainQuest.questTargetList
        val mainQuestID = mainQuest.mainQuestID
        mainQuest.subQuestList.forEach {
            val subQuestID = it.subQuestID
            val subQuestData = QuestSubData(questID, mainQuestID, subQuestID, it.questTargetList, QuestState.NOT_ACCEPT)
            subQuestDataList[subQuestID] = subQuestData
        }
        val mainQuestData = QuestMainData(questID, mainQuestID, subQuestDataList, mainTargetList, state)
        val questData = QuestData(questID, mainQuestData, 0, state)
        pData.questDataList[questID] = questData
        saveControl(player, pData, mainQuestData)
        runControl(pData, questID, mainQuestID, "")
    }

    /**
     * 存储控制模块
     */
    fun saveControl(player: Player, pData: PlayerData, questOpenData: QuestOpenData) {
        if (questOpenData.state != QuestState.DOING) return
        val questID = questOpenData.questID
        val mainQuestID = questOpenData.mainQuestID
        val subQuestID = questOpenData.subQuestID
        val scriptList: MutableList<String>
        val controlID: String
        if (subQuestID == "") {
            val mModule = getMainQuestModule(questID, mainQuestID)?: return
            val cModule = mModule.questControl
            controlID = cModule.controlID
            scriptList = cModule.scriptList
        }else {
            val mModule = getSubQuestModule(questID, mainQuestID, subQuestID)?: return
            val cModule = mModule.questControl
            controlID = cModule.controlID
            scriptList = cModule.scriptList
        }
        if (controlID == "") return
        val controlData = QuestControlData(player, questOpenData, scriptList, 0, 0)
        pData.controlList[controlID] = controlData
    }

    fun generateControlID(questID: String, mainQuestID: String, subQuestID: String): String {
        return "[$questID]-[$mainQuestID]-[$subQuestID]"
    }

    /**
     * 运行控制模块
     */
    fun runControl(player: Player, questID: String, mainQuestID: String, subQuestID: String) {
        val pData = DataStorage.getPlayerData(player)?: return
        runControl(pData, questID, mainQuestID, subQuestID)
    }

    fun runControl(pData: PlayerData, questID: String, mainQuestID: String, subQuestID: String) {
        val id = generateControlID(questID, mainQuestID, subQuestID)
        val control = pData.controlList[id]?: return
        control.runScript()
    }

    /**
     * 检索是否已有处于 DOING 状态的主线任务
     */
    fun hasDoingMainQuest(pData: PlayerData): Boolean {
        val questData = pData.questDataList
        questData.forEach { (_, u)->
            if (u.questMainData.state == QuestState.DOING) return true
        }
        return false
    }

    /**
     * 结束任务，最终结束
     * 成功脚本在目标完成时运行
     *
     * @param state 设定任务成功与否
     * @param runFailReward 如果失败，是否执行当前主线任务失败脚本
     */
    fun endQuest(player: Player, questID: String, state: QuestState, runFailReward: Boolean) {
        val questData = getQuestData(player, questID)?: return
        if (state == QuestState.FAILURE && runFailReward) {
            val mainQuestID = questData.questMainData.mainQuestID
            val failReward = getReward(questID, mainQuestID, "", "", state)?: return
            failReward.forEach {
                KetherHandler.eval(player, it)
            }
        }
    }

    /**
     * 结束当前主线任务，执行下一个主线任务或最终完成
     */
    fun finishMainQuest(player: Player, questID: String, mainQuestID: String) {
        val questData = getQuestData(player, questID)?: return
        val questMainModule = getMainQuestModule(questID, mainQuestID)?: return
        val nextMainID = questMainModule.nextMinQuestID
        if (nextMainID == "") {
            questData.state = QuestState.FINISH
        }else {
            acceptNextMainQuest(player, questData, nextMainID)
        }
    }

    /**
     * 完成当前主线任务的一个支线任务
     * 只设定状态
     */
    fun finishSubQuest(player: Player, questID: String, subQuestID: String) {
        val subQuestData = getSubQuestData(player, questID, subQuestID)?: return
        subQuestData.state = QuestState.FINISH
    }

    /**
     * 获得玩家任务数据
     */
    fun getQuestData(player: Player, questID: String): QuestData? {
        val pData = DataStorage.getPlayerData(player) ?: return null
        return pData.questDataList[questID]
    }

    /**
     * 获得玩家当前主线任务数据
     */
    fun getMainQuestData(player: Player, questID: String): QuestOpenData? {
        val questData = getQuestData(player, questID)?: return null
        return questData.questMainData
    }

    /**
     * 获得玩家支线任务数据
     */
    fun getSubQuestData(player: Player, questID: String, subQuestID: String): QuestOpenData? {
        val mainQuestData = getMainQuestData(player, questID)?: return null
        return mainQuestData.questSubList[subQuestID]
    }

    /**
     * 得到奖励脚本，成功与否
     * 成功的一般是在目标完成时得到
     */
    fun getReward(questID: String, mainQuestID: String, subQuestID: String, rewardID: String, type: QuestState): MutableList<String>? {
        val questModule = questMap[questID]!!
        if (subQuestID == "") {
            for (m in questModule.mainQuestList) {
                if (m.mainQuestID == mainQuestID) {
                    return if (type == QuestState.FINISH) {
                        m.questReward.finishReward[rewardID]!!
                    }else m.questReward.failReward
                }
            }
        }
        for (m in questModule.mainQuestList) {
            if (m.mainQuestID == mainQuestID) {
                for (s in m.subQuestList) {
                    if (s.subQuestID == subQuestID) {
                        return if (type == QuestState.FINISH) {
                            s.questReward.finishReward[rewardID]!!
                        }else s.questReward.failReward
                    }
                }
            }
        }
        return null
    }

    /**
     * 获得触发的主线任务目标
     */
    fun getDoingMainTarget(player: Player, name: String): QuestTarget? {
        val questData = getDoingQuest(player)?: return null
        val mainData = questData.questMainData
        return mainData.targetList[name]
    }

    /**
     * 获得触发的支线任务目标及其支线任务数据
     */
    fun getDoingSubTarget(player: Player, name: String): TargetSubData? {
        val questData = getDoingQuest(player)?: return null
        val mainData = questData.questMainData
        mainData.questSubList.forEach { (t, u) ->
            if (u.state == QuestState.DOING) {
                if (u.targetList.containsKey(name)) {
                    val tg = u.targetList[name]?: return null
                    return TargetSubData(t, tg)
                }
            }
        }
        return null
    }

    /**
     * 获得正在进行中的任务
     */
    fun getDoingQuest(player: Player): QuestData? {
        val pData = DataStorage.getPlayerData(player)?: return null
        if (pData.questDataList.isEmpty()) return null
        pData.questDataList.forEach { (_, questData) ->
            if (questData.state == QuestState.DOING) {
                return questData
            }
        }
        return null
    }

}