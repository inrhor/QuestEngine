package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState

class QuestInnerData(
    val questID: String,
    val innerQuestID: String,
    var targetsData: MutableMap<String, TargetData>,
    var state: QuestState,
    var rewardState: MutableMap<String, Boolean>) {

    constructor(questID: String,
                innerQuestID: String,
                targetList: MutableMap<String, TargetData>,
                state: QuestState):
            this(questID, innerQuestID, targetList, state, mutableMapOf())


}