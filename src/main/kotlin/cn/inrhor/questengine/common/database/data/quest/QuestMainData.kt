package cn.inrhor.questengine.common.database.data.quest

/**
 * 玩家主线任务数据
 *
 * @param questSubList 支线任务数据列表
 * @param schedule 当前主线任务进度
 * @param time 当前主线任务时间， 以秒为单位
 */
class QuestMainData(
    val questID: String,
    val mainQuestID: String,
    var questSubList: MutableMap<String, QuestSubData>,
    var schedule: Int,
    var time: Int) {
}