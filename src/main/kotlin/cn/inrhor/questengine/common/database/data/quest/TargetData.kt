package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame

/**
 * 任务目标存储
 */
data class TargetData(
    val id: String ="?",
    val questID: String = "?",
    var schedule: Int = 0,
    var state: StateType = StateType.DOING) {

    constructor(questID: String, target: TargetFrame): this(target.id, questID)

    fun getTargetFrame(): TargetFrame {
        questID.getQuestFrame().target.forEach {
            if (it.id == id) return it
        }
        error("null target frame: $id($questID)")
    }

}