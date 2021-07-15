package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState

class QuestMainData(
    override val questID: String,
    override val mainQuestID: String,
    override var questSubList: MutableMap<String, QuestSubData>,
    override var targetsData: MutableMap<String, TargetData>,
    override var state: QuestState,
    override var rewardState: MutableMap<String, Boolean>): QuestOpenData() {

    constructor(questID: String,
                mainQuestID: String,
                questSubList: MutableMap<String, QuestSubData>,
                targetList: MutableMap<String, TargetData>,
                state: QuestState):
            this(questID, mainQuestID, questSubList, targetList, state, mutableMapOf())


}