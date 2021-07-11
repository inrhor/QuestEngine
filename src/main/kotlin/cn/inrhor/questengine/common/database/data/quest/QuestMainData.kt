package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

class QuestMainData(
    override val questID: String,
    override val mainQuestID: String,
    override var questSubList: MutableMap<String, QuestSubData>,
    override var schedule: MutableMap<String, Int>,
    override var targetList: MutableMap<String, QuestTarget>,
    override var state: QuestState,
    override var rewardState: MutableMap<String, Boolean>): QuestOpenData() {

    constructor(questID: String,
                mainQuestID: String,
                questSubList: MutableMap<String, QuestSubData>,
                targetList: MutableMap<String, QuestTarget>,
                state: QuestState):
            this(questID, mainQuestID, questSubList, mutableMapOf(), targetList, state, mutableMapOf())


}