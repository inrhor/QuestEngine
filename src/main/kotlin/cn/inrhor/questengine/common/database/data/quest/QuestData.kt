package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.quest.QuestState
import java.util.*

/**
 * 玩家一个任务数据
 *
 * @param questID
 * @param questInnerData 正在进行的内部任务
 */
class QuestData(
    val questUUID: UUID,
    val questID: String,
    val questInnerData: QuestInnerData,
    var state: QuestState,
    var teamData: TeamOpen?,
    val finishedList: MutableList<String>)