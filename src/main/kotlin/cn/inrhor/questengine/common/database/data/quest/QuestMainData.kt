package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

/**
 * 玩家主线任务数据
 *
 * @param questSubList 支线任务数据列表
 * @param schedule 当前主线任务进度
 * @param targetList 当前主线任务目标
 */
class QuestMainData(
    val questID: String,
    val mainQuestID: String,
    var questSubList: MutableMap<String, QuestSubData>,
    var schedule: MutableMap<String, Int>,
    var targetList: MutableMap<String, QuestTarget>,
    var state: QuestState,
    var rewardState: MutableMap<String, Boolean>) {

    constructor(questID: String,
                mainQuestID: String,
                questSubList: MutableMap<String, QuestSubData>,
                targetList: MutableMap<String, QuestTarget>,
                state: QuestState):
            this(questID, mainQuestID, questSubList, mutableMapOf(), targetList, state, mutableMapOf())
}