package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState

class QuestSubData(
    override val questID: String,
    override val mainQuestID: String,
    override var subQuestID: String,
    override var targetsData: MutableMap<String, TargetData>,
    override var state: QuestState,
    override var rewardState: MutableMap<String, Boolean>): QuestOpenData() {

    constructor(questID: String,
                mainQuestID: String,
                subQuestID: String,
                targetList: MutableMap<String, TargetData>,
                state: QuestState):
            this(questID, mainQuestID, subQuestID, targetList, state, mutableMapOf())
}