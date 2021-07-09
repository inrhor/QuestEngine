package cn.inrhor.questengine.common.database.data.quest

/**
 * 玩家支线任务数据
 */
class QuestSubData(
    val questID: String,
    val mainQuestID: String,
    val subQuestID: String,
    val schedule: Int) {
}