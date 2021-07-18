package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState

abstract class QuestOpenData {

    abstract val questID: String

    abstract val mainQuestID: String

    open var subQuestID: String = ""

    open var questSubList: MutableMap<String, QuestSubData> = mutableMapOf()

    abstract var targetsData: MutableMap<String, TargetData>

    abstract var state: QuestState

    abstract var rewardState: MutableMap<String, Boolean>

}