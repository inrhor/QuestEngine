package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.enum.StateType

data class DataContainer(
    var quest: MutableMap<String, QuestData> = mutableMapOf(), var tags: TagsData = TagsData()
) {

    /**
     * 注册新任务
     * 覆盖原有任务数据
     */
    fun installQuest(questFrame: QuestFrame) {
        quest[questFrame.id] = QuestData(questFrame)
    }

    /**
     * 卸载任务
     */
    fun unloadQuest(questID: String) {
        quest.remove(questID)
    }

    /**
     * 修改任务状态
     */
    fun toggleQuest(questID: String, state: StateType = StateType.DOING) {
        quest[questID]?.state = state
    }

}
