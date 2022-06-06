package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import java.util.*

/**
 * 玩家一个任务数据
 *
 * @param id
 */
data class GroupData(
    val uuid: UUID = UUID.randomUUID(),
    val id: String = "?",
    var state: QuestState = QuestState.DOING,
    val quest: MutableList<QuestData> = mutableListOf())