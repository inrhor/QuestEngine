package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget

/**
 * 玩家支线任务数据
 */
class QuestSubData(
    val questID: String,
    val mainQuestID: String,
    val subQuestID: String,
    val schedule: Int,
    var targetList: MutableList<QuestTarget>,
    var state: QuestState) {
}