package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.dialog.FlagsDialog
import cn.inrhor.questengine.common.dialog.theme.chat.ChatCache
import cn.inrhor.questengine.common.nav.NavData
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
    val navData: MutableMap<String, NavData> = mutableMapOf(),
    val flagsDialog: MutableSet<FlagsDialog> = mutableSetOf(),
    var input: InputData? = null
)