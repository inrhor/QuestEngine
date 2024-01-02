package cn.inrhor.questengine.api.event.data

import cn.inrhor.questengine.common.nav.NavData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class NavigationDataEvent {

    /**
     * 更新导航数据状态
     */
    class UpdateState(val player: Player, val navData: NavData): BukkitProxyEvent()

    /**
     * 删除导航数据
     */
    class Remove(val player: Player, val navData: NavData): BukkitProxyEvent()

}