package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

class QuestSubData(
    override val questID: String,
    override val mainQuestID: String,
    override var subQuestID: String,
    override var schedule: MutableMap<String, Int>,
    override var targetList: MutableMap<String, QuestTarget>,
    override var state: QuestState,
    override var rewardState: MutableMap<String, Boolean>): QuestOpenData() {

    constructor(questID: String,
                mainQuestID: String,
                subQuestID: String,
                targetList: MutableMap<String, QuestTarget>,
                state: QuestState):
            this(questID, mainQuestID, subQuestID, mutableMapOf(), targetList, state, mutableMapOf())
}