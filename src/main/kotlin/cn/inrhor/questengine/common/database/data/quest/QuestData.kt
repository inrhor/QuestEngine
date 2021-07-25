package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.quest.QuestState

/**
 * 玩家一个任务数据
 *
 * @param questID
 * @param questInnerData 正在进行的内部任务
 */
class QuestData(
    val questID: String,
    val questInnerData: QuestInnerData,
    var state: QuestState,
    var teamData: TeamOpen?,
    var finishedList: MutableList<String>) {
}