package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.enum.StateType

/**
 * 任务目标存储
 */
data class TargetData(
    val id: String,
    val questID: String,
    var schedule: Int,
    var state: StateType = StateType.DOING)