package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestState
import java.util.*

class QuestInnerData(
    val questID: String,
    val innerQuestID: String,
    var targetsData: MutableMap<String, TargetData>,
    var state: QuestState, var timeDate: Date, var endTimeDate: Date?,
    var rewardState: MutableMap<String, Boolean> = mutableMapOf())