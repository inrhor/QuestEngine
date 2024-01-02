package cn.inrhor.questengine.api.event

import cn.inrhor.questengine.common.nav.NavData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class NavigationEvent {

    /**
     * 更新导航数据状态
     */
    class UpdateState(val player: Player, val navData: NavData): BukkitProxyEvent()

}