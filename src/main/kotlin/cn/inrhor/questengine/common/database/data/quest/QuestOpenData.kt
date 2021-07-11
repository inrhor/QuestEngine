package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

abstract class QuestOpenData() {

    abstract val questID: String

    abstract val mainQuestID: String

    open lateinit var subQuestID: String

    open lateinit var questSubList: MutableMap<String, QuestSubData>

    abstract var schedule: MutableMap<String, Int>

    abstract var targetList: MutableMap<String, QuestTarget>

    abstract var state: QuestState

    abstract var rewardState: MutableMap<String, Boolean>

}