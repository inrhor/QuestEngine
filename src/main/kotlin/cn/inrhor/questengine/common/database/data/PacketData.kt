package cn.inrhor.questengine.common.database.data

import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.common.packet.action.ClickActionData
import org.bukkit.Location

/**
 * 玩家数据包数据
 *
 * 主要应用于 交互 删除 实体数据包
 */
class PacketData(val packetModule: PacketModule, val entityID: Int, val location: Location) {

    // 交互动作
    val clickAction = ClickActionData(0)

}