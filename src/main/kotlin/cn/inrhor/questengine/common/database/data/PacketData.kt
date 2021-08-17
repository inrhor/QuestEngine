package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.common.packet.action.ClickAction
import org.bukkit.Location

/**
 * 玩家数据包数据
 *
 * 主要应用于 交互 删除 实体数据包
 */
class PacketData(val packetID: String, val entityID: Int, val location: Location) {

    // 交互动作
    val clickAction = ClickAction(0)

}