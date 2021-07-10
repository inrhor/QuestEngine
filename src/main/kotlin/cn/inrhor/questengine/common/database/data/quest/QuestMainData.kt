package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestTarget

/**
 * 玩家主线任务数据
 *
 * @param questSubList 支线任务数据列表
 * @param schedule 当前主线任务进度
 * @param targetList 当前主线任务目标
 */
class QuestMainData(
    val questID: String,
    val mainQuestID: String,
    var questSubList: MutableMap<String, QuestSubData>,
    var schedule: Int,
    var targetList: MutableList<QuestTarget>) {
}