package cn.inrhor.questengine.common.database.data.quest.remove

import cn.inrhor.questengine.common.database.data.quest.remove.QuestSubDataL
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

/**
 * 玩家主线任务数据
 *
 * @param questSubList 支线任务数据列表
 * @param schedule 当前主线任务进度
 * @param targetList 当前主线任务目标
 * @param rewardState 是否已接受某奖励
 */
class QuestMainDataL(
    val questID: String,
    val mainQuestID: String,
    var questSubList: MutableMap<String, QuestSubDataL>,
    var schedule: MutableMap<String, Int>,
    var targetList: MutableMap<String, QuestTarget>,
    var state: QuestState,
    var rewardState: MutableMap<String, Boolean>) {

    constructor(questID: String,
                mainQuestID: String,
                questSubList: MutableMap<String, QuestSubDataL>,
                targetList: MutableMap<String, QuestTarget>,
                state: QuestState):
            this(questID, mainQuestID, questSubList, mutableMapOf(), targetList, state, mutableMapOf())
}