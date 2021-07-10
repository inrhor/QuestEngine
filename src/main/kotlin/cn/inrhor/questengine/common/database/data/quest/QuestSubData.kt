package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

/**
 * 玩家支线任务数据
 *
 * @param rewardState 是否已接受某奖励
 */
class QuestSubData(
    val questID: String,
    val mainQuestID: String,
    val subQuestID: String,
    var schedule: MutableMap<String, Int>,
    var targetList: MutableMap<String, QuestTarget>,
    var state: QuestState,
    var rewardState: MutableMap<String, Boolean>) {

    constructor(questID: String,
                mainQuestID: String,
                subQuestID: String,
                targetList: MutableMap<String, QuestTarget>,
                state: QuestState):
            this(questID, mainQuestID, subQuestID, mutableMapOf(), targetList, state, mutableMapOf())
}