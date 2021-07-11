package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState

/**
 * 玩家一个任务数据
 *
 * @param questID
 * @param questMainData 正在进行的主线任务
 * @param schedule 总进度
 */
class QuestData(
    val questID: String,
    val questMainData: QuestOpenData,
    var schedule: Int,
    var state: QuestState) {
}