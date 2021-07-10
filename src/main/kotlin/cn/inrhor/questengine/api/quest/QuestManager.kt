package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestMainData
import cn.inrhor.questengine.common.database.data.quest.QuestSubData
import cn.inrhor.questengine.common.kether.KetherHandler
import org.bukkit.entity.Player
import java.util.HashMap
import java.util.LinkedHashMap

class QuestManager {

    companion object {
        /**
         * 注册的任务
         */
        private var questMap: HashMap<String, QuestModule> = LinkedHashMap()
    }

    fun register(questID: String, questModule: QuestModule) {
        questMap[questID] = questModule
    }

    fun getQuestModule(questID: String): QuestModule? {
        return questMap[questID]
    }

    fun getMainQuestModule(questID: String, mainQuestID: String): QuestMainModule? {
        val questModule = questMap[questID]?: return null
        questModule.mainQuestList.forEach {
            if (it.mainQuestID == mainQuestID) return it
        }
        return null
    }

    fun acceptQuest(player: Player, questID: String) {
        val pData = DataStorage().getPlayerData(player)?: return
        val questModule = getQuestModule(questID)?: return
        val startMainID = questModule.startMainQuestID
        val startMainQuest = questModule.getStartMainQuest()?: return
        val subQuestDataList = mutableMapOf<String, QuestSubData>()
        val mainTargetList = startMainQuest.questTargetList
        startMainQuest.subQuestList.forEach {
            val subQuestID = it.subQuestID
            val subQuestData = QuestSubData(questID, startMainID, subQuestID, 0, it.questTargetList)
            subQuestDataList[subQuestID] = subQuestData
        }
        val mainQuestData = QuestMainData(questID, startMainID, subQuestDataList, 0, mainTargetList)
        val questData = QuestData(questID, mainQuestData, 0)
        pData.questDataList[questID] = questData
    }

    /**
     * 结束任务，认定任务失败
     *
     * @param runFailReward 是否执行当前主线任务的失败脚本
     */
    fun endQuestFail(player: Player, questID: String, runFailReward: Boolean) {
        val pData = DataStorage().getPlayerData(player)?: return
        val questData = pData.questDataList[questID]?: return
        if (runFailReward) {
            val mainQuestID = questData.questMainData.mainQuestID
            val questMainModule = getMainQuestModule(questID, mainQuestID)?: return
            questMainModule.questReward.failReward.forEach {
                KetherHandler.eval(player, it)
            }
        }
    }

    /**
     * 结束当前主线任务，执行下一个主线任务或最终完成
     */
    fun finishMainQuest() {

    }

    /**
     * 完成支线任务
     */
    fun finishSubQuest() {

    }

    /**
     * 结束任务
     */
    fun endQuest() {

    }

    fun getReward(questID: String, mainQuestID: String, subQuestID: String, rewardID: String, type: String): MutableList<String>? {
        val questModule = questMap[questID]!!
        if (subQuestID == "") {
            for (m in questModule.mainQuestList) {
                if (m.mainQuestID == mainQuestID) {
                    return if (type == "finish") {
                        m.questReward.finishReward[rewardID]!!
                    }else m.questReward.failReward
                }
            }
        }
        for (m in questModule.mainQuestList) {
            if (m.mainQuestID == mainQuestID) {
                for (s in m.subQuestList) {
                    if (s.subQuestID == subQuestID) {
                        return if (type == "finish") {
                            s.questReward.finishReward[rewardID]!!
                        }else s.questReward.failReward
                    }
                }
            }
        }
        return null
    }

}