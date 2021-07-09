package cn.inrhor.questengine.common.database.data.quest

/**
 * 玩家一个任务数据
 *
 * @param questID
 * @param questMainData 正在进行的主线任务
 * @param schedule 总进度
 */
class QuestData(
    val questID: String,
    val questMainData: QuestMainData,
    var schedule: Int) {
}