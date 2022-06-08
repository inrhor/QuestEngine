package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.dialog.theme.chat.ChatCache
import cn.inrhor.questengine.common.nav.NavData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player
import java.util.*

/**
 * @param uuid 玩家UUID
 * @param dialogData 对话数据
 * @param dataContainer 数据存储
 */
data class PlayerData(
    val uuid: UUID,
    var teamData: TeamOpen? = null,
    val dialogData: DialogData = DialogData(),
    var dataContainer: DataContainer = DataContainer(),
    val chatCache: ChatCache = ChatCache(),
    val navData: MutableMap<String, NavData> = mutableMapOf())

/**
 * @return 玩家队伍
 */
fun Player.teamData(): TeamOpen? {
    return getPlayerData().teamData
}

/**
 * @return 是否存在任务数据
 */
fun Player.existQuestData(questID: String): Boolean {
    return getPlayerData().dataContainer.quest.containsKey(questID)
}

/**
 * @return 任务数据
 */
fun Player.questData(questID: String): QuestData {
    return getPlayerData().dataContainer.quest[questID]?: error("null quest data: $questID")
}

/**
 * @return 标签数据
 */
fun Player.tagsData(): TagsData {
    return getPlayerData().dataContainer.tags
}

/**
 * @return 目标数据
 */
fun Player.targetData(questID: String, targetID: String): TargetData {
    questData(questID).target.forEach {
        if (it.id == targetID) return it
    }
    error("null target data: $targetID($questID)")
}

/**
 * @return 是否完成任务的所有目标
 */
fun Player.completedTargets(questID: String, modeType: ModeType): Boolean {
    if (modeType == ModeType.COLLABORATION) {
        teamData()?.playerMembers()?.forEach {
            if (!it.completedTarget(questID)) return false
        }
    }else {
        return completedTarget(questID)
    }
    return true
}

fun Player.completedTarget(questID: String): Boolean {
    questData(questID).target.forEach {
        if (it.state != StateType.FINISH) return false
    }
    return true
}