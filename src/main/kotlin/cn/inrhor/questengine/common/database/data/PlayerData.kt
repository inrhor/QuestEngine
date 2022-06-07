package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.theme.chat.ChatCache
import cn.inrhor.questengine.common.nav.NavData
import org.bukkit.entity.Player
import java.util.*

/**
 * @param uuid 玩家UUID
 * @param dialogData 对话数据
 * @param controlQueue 控制模块队列
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
    return this.getPlayerData().teamData
}