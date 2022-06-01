package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.dialog.theme.chat.ChatCache
import cn.inrhor.questengine.common.nav.NavData
import org.bukkit.entity.Player
import java.util.*

/**
 * @param uuid 玩家UUID
 * @param dialogData 对话数据
 * @param questDataList 任务数据集合
 * @param controlData 控制模块集合
 */
data class PlayerData(
    val uuid: UUID,
    var teamData: TeamOpen?,
    val dialogData: DialogData,
    var questDataList: MutableMap<UUID, QuestData>, /* QuestUUID 对应 QuestData */
    var controlData: ControlData,
    val tagsData: TagsData = TagsData(),
    val chatCache: ChatCache = ChatCache(),
    val navData: MutableMap<String, NavData> = mutableMapOf()) {

    constructor(uuid: UUID):
            this(uuid, null,
                DialogData(mutableMapOf(), mutableMapOf(), mutableMapOf()),
                mutableMapOf(),
                ControlData(linkedMapOf(), mutableMapOf())
            )

}

/**
 * @return 玩家队伍
 */
fun Player.teamData(): TeamOpen? {
    return DataStorage.getPlayerData(this).teamData
}