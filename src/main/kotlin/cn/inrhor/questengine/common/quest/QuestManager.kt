package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.api.quest.TargetExtend
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