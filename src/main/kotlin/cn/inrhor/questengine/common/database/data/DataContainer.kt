package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.utlis.time.toStr
import java.util.*

data class StorageData(val key: String, var value: String) {

}

data class TrackData(val questID: String = "", val targetID: String = "")

data class DataContainer(
    val quest: MutableMap<String, QuestData> = mutableMapOf(), var tags: TagsData = TagsData(),
    var storage: MutableList<StorageData> = mutableListOf(), var trackData: TrackData = TrackData()
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
    fun toggleQuest(questID: String, state: StateType = StateType.DOING): DataContainer {
        quest[questID]?.state = state
        return this
    }

    /**
     * 完成任务时间
     */
    fun finishTime(questID: String): DataContainer {
        quest[questID]?.end = Date().toStr()
        return this
    }

}
