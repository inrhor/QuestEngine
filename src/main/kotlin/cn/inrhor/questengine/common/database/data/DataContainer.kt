package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.event.QuestDataEvent
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.utlis.time.toStr
import org.bukkit.entity.Player
import java.util.*

data class TrackData(val questID: String = "", val targetID: String = "")

data class DataContainer(
    val quest: MutableMap<String, QuestData> = mutableMapOf(), var tags: TagsData = TagsData(),
    var storage: MutableMap<String, String> = mutableMapOf(), var trackData: TrackData = TrackData()
) {

    /**
     * 注册新任务
     * 覆盖原有任务数据
     */
    fun installQuest(player: Player, questFrame: QuestFrame) {
        val questData = QuestData(questFrame)
        quest[questFrame.id] = questData
        QuestDataEvent.Install(player, questData).call()
    }

    /**
     * 卸载任务
     */
    fun unloadQuest(player: Player, questID: String) {
        quest.remove(questID)
        QuestDataEvent.Unload(player, questID).call()
    }

    /**
     * 修改任务状态
     */
    fun toggleQuest(player: Player, questID: String, state: StateType = StateType.DOING): DataContainer {
        val questData = quest[questID]?: return this
        questData.state = state
        QuestDataEvent.ToggleState(player, questData).call()
        return this
    }

    /**
     * 完成任务时间
     */
    fun finishTime(player: Player, questID: String): DataContainer {
        val questData = quest[questID]?: return this
        questData.end = Date().toStr()
        QuestDataEvent.ToggleFinishTime(player, questData).call()
        return this
    }

}
