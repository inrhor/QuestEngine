package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import java.util.*

data class DataContainer(
    var quest: MutableMap<String, QuestData> = mutableMapOf(), var tags: TagsData = TagsData()
) {

    /**
     * 注册新任务
     * 覆盖原有任务数据
     */
    fun installQuest(player: Player, questFrame: QuestFrame) {
        val questData = QuestData(questFrame)
        quest[questFrame.id] = questData
        questData.updateTime(player)
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
